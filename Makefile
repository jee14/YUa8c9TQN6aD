init:
	git config core.hooksPath .githooks
	chmod +x .githooks/pre-commit
	git update-index --chmod=+x .githooks/pre-commit