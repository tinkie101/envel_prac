.DEFAULT_GOAL := help

dev-up: ## Run docker environment
	docker-compose -f ./env/docker-compose.yml up -d

dev-down: ## Stop docker environment
	docker-compose -f ./env/docker-compose.yml up -d

help: ## This help.
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)
