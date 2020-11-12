# Empressia Gradle Plugin

## 目次

* [概要](#概要)
* [使い方](#使い方)
* [使用しているライブラリ](#使用しているライブラリ)
* [ライセンス](#ライセンス)

## 概要

Empressia製のGradle用プラグインです。  
『あ～こんなことしたいな～』みたいな、分類できない雑多なものを詰め込みます。  

## 使い方

### プラグインを適用します。  

```groovy
plugins {
	id "jp.empressia.gradle.plugin.misc" version "1.0.0";
}
```

※上記のサンプルのバージョンは、最新ではない可能性があります。  

### どんなことできるの？

* CopySyncタスクでファイル単位でコピーする。

	ファイル単位のコピー＆同期機能を持つタスク定義があります。  
	インクリメンタルに処理されている限り、出力先のディレクトリの関係ないファイルが削除されることはありません。  
	※インクリメンタルに処理されない場合は削除される場合があります（Gradleの動作依存です）。  

	```groovy
	// サンプル：実行時のクラスパスをビルド出力先のディレクトリにコピーするタスクを作成します。
	task copySyncSomething(type: jp.empressia.gradle.task.CopySync) {
		from = configurations.runtimeClasspath;
		into = file("$buildDir");
	}
	```

* copySyncModulesタスクを使用する。

	デフォルトで、configurations.runtimeClasspathを$buildDir/moduleに、CopySyncタスクを使用してコピーします。  
	カスタマイズする場合は、例えば以下のような書式になります。
	```groovy
	empressia {
		copySyncModules {
			from = configurations.runtimeClasspath;
			into = file("$buildDir/module");
		}
	}
	```

* Visual Studio Codeで、簡単にGradleとJavaのModule対応した起動設定を用意する。

	Visual Studio CodeのJavaの拡張機能は、GradleのクラスパスをうまくModuleに追加してくれません。  
	これを解決するために、このプラグインのcopySyncModulesタスクを起動前に呼んで、結果を--module-pathに追加することで、起動を簡単にします。

	.vscode/tasks.jsonに、以下のように記載します。
	commandでGradleのcopySyncModulesを呼ぶのがポイントです。
	```json
	{
		"version": "2.0.0",
		"tasks": [
			{
				"label": "CopySyncModules",
				"type": "shell",
				"presentation": {
					"reveal": "silent"
				},
				"command": "./gradlew copySyncModules"
			}
		]
	}
	```
	.vscode/launch.jsonのに、以下のように記載します。  
	preLaunchTaskに上記タスクのラベルの値を設定し、  
	modulePathsに、通常生成されるクラスのディレクトリと、  
	上記タスクでコピーされる出力先のディレクトリを設定するのがポイントです。  
	```json
	{
		"version": "0.2.0",
		"configurations": [
			{
				"type": "java",
				"name": "Debug (Launch) - Current File",
				"request": "launch",
				"mainClass": "${file}",
				"preLaunchTask": "CopySyncModules",
				"modulePaths": ["bin/main/", "build/module/"]
			}
		]
	}
	```
	これで、Visual Studio CodeからのJavaアプリケーション起動時に、  
	必要なモジュールが読み込まれます。  


## ライセンス

いつも通りのライセンスです。  
zlibライセンス、MITライセンスでも利用できます。  

ただし、チーム（複数人）で使用する場合は、MITライセンスとしてください。  

## 使用しているライブラリ

* Picocog
	* https://github.com/ainslec/picocog

※Picocogは、独自にカスタマイズしたバージョンを使用しています。
