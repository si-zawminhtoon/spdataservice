![https://github.com/si-zawminhtoon/spdataservice/actions?query=workflow:CI](https://github.com/si-zawminhtoon/spdataservice/actions/workflows/continuous-integration-workflow.yml/badge.svg?event=push)

# SPデータサービス
機能確認用調査

## ダウンロード
- [Docker Desktop for Windows](https://docs.docker.com/desktop/install/windows-install/)
- [Visual Studio Code](https://azure.microsoft.com/ja-jp/products/visual-studio-code)

## Visual Studio Codeにインストールする拡張子
1. Debugger for Java
1. Extension Pack for Java
1. Spring Boot Extension Pack

## 実行手順（Visual Studio Code利用）
1. <kbd>Ctrl</kbd>+<kbd>Shift</kbd>+<kbd>G</kbd> 押下
1. リポジトリのクローン
1. GitHub から複製
1. `https://github.com/si-zawminhtoon/spdataservice`を選択
1. クローンしたリポジトリを開く
1. <kbd>Ctrl</kbd>+<kbd>@</kbd> 押下
1. `docker-compose build` 実行
1. `docker-compose up -d` 実行

## 確認方法
1. [http://localhost:8081/](http://localhost:8081/)

## 終了手順
1. `docker-compose down` 実行