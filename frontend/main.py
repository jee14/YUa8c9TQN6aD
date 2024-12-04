import streamlit as st
import asyncio
import aiohttp
from typing import AsyncGenerator

st.set_page_config(page_title="네이버 스마트스토어 FAQ", page_icon="🤖")
st.title("네이버 스마트스토어 도움말")
st.caption("네이버 스마트스토어의 자주 묻는 질문을 바탕으로 답변해드립니다.")

if 'message_list' not in st.session_state:
    st.session_state.message_list = []

for message in st.session_state.message_list:
    with st.chat_message(message["role"]):
        st.write(message["content"])

async def get_ai_response(user_question: str) -> AsyncGenerator[str, None]:
    url = "http://localhost:8080/chats"
    headers = {
        "Content-Type": "application/json",
        "Accept": "text/event-stream",
    }
    data = {"input": user_question}

    try:
        async with aiohttp.ClientSession() as session:
            async with session.post(url, json=data, headers=headers) as response:
                if response.status == 200:
                    async for line in response.content:
                        decoded_line = line.decode("utf-8").rstrip()
                        if decoded_line.startswith("data:"):
                            content = decoded_line[5:]
                            if len(content.strip()) == 0:
                                yield "\n"
                            else:
                                yield content
                else:
                    raise Exception(f"응답 코드: {response.status}")
    except Exception as e:
        st.error(f"AI 응답 생성 중 오류가 발생했습니다: {e}")
        raise e

async def async_next(generator: AsyncGenerator):
    try:
        return await generator.__anext__()
    except StopAsyncIteration:
        raise

def to_sync_generator(async_gen: AsyncGenerator):
    loop = asyncio.new_event_loop()
    asyncio.set_event_loop(loop)

    try:
        while True:
            try:
                yield loop.run_until_complete(async_next(async_gen))
            except StopAsyncIteration:
                break
    finally:
        loop.close()

def main():
    if user_question := st.chat_input(placeholder="무엇이 궁금하신가요?"):
        with st.chat_message("user"):
            st.write(user_question)
        st.session_state.message_list.append({"role": "user", "content": user_question})
        ai_response = to_sync_generator(get_ai_response(user_question))
        with st.chat_message("ai"):
            ai_message = st.write_stream(ai_response)
            st.session_state.message_list.append({"role": "ai", "content": ai_message})

main()
