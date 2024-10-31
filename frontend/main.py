import streamlit as st
import asyncio
import websockets
import re

st.set_page_config(page_title="네이버 스마트스토어 FAQ", page_icon="🤖")

st.title("네이버 스마트스토어 도움말")
st.caption("네이버 스마트스토어의 자주 묻는 질문을 바탕으로 답변해드립니다.")

def initialize_session_state():
    if 'message_list' not in st.session_state:
        st.session_state.message_list = []

def display_messages():
    for message in st.session_state.message_list:
        with st.chat_message(message["role"]):
            st.write(message["content"])

def handle_user_input():
    if user_question := st.chat_input(placeholder="무엇이 궁금하신가요?"):
        with st.chat_message("user"):
            st.write(user_question)
        st.session_state.message_list.append({"role": "user", "content": user_question})
        with st.spinner("답변을 생성하는 중입니다."):
            try:
                ai_response = send_command_async(user_question)
                with st.chat_message("ai"):
                    st.write(ai_response)
                st.session_state.message_list.append({"role": "ai", "content": ai_response})
            except Exception as e:
                st.error(f"AI 응답 생성 중 오류가 발생했습니다: {e}")

async def send_command(command):
    stomp_connect = "CONNECT\naccept-version:1.2\n\n\0"
    stomp_subscribe = f"SUBSCRIBE\ndestination:/subscribes/1\nid:1\n\n\0"
    stomp_frame = f"SEND\ndestination:/publishes/messages/1\n\n{command}\0"

    try:
        async with websockets.connect("ws://localhost:8080/chats") as websocket:
            await websocket.send(stomp_connect)  # STOMP 연결 초기화
            print("Connected to WebSocket server")

            await websocket.send(stomp_subscribe)  # 구독 경로 설정
            print("Subscribed to response channel")

            await websocket.send(stomp_frame)  # 사용자 명령어 전송
            print("Command sent")

            # 서버로부터 응답을 기다리고 수신
            while True:
                raw_response = await websocket.recv()

                # `MESSAGE` 프레임이 있는 경우에만 본문 추출
                if raw_response.startswith("MESSAGE"):
                    body_match = re.search(r"\n\n(.*?)\0", raw_response, re.DOTALL)
                    if body_match:
                        body = body_match.group(1)
                        return body  # 본문만 반환
                else:
                    print("Non-MESSAGE frame received, ignoring.")

    except websockets.exceptions.ConnectionClosedError:
        print("Connection to WebSocket server lost. Reconnecting...")
        return await send_command(command)  # 재연결 시도
    except Exception as e:
        print(f"Error sending command: {e}")
        raise e

def send_command_async(command):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)
    return loop.run_until_complete(send_command(command))  # 비동기 함수 결과 반환

# Initialize session state
initialize_session_state()

# Display all previous messages
display_messages()

# Handle new user input
handle_user_input()
