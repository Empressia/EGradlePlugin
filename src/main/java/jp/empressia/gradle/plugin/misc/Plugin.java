package jp.empressia.gradle.plugin.misc;

import java.io.File;
import java.nio.file.Path;

import javax.inject.Inject;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;

import jp.empressia.gradle.task.CopySync;

/**
 * Empressia製のGradle用のプラグインです。
 * @author すふぃあ
 */
public class Plugin implements org.gradle.api.Plugin<Project> {
	
	private static String EXTENSION_NAME = "empressia";
	private static String TASK_NAME = "copySyncModules";

	/** コンストラクタです。 */
	public Plugin() {
	}

	public void apply(Project project) {
		Extension extension = project.getExtensions().create(EXTENSION_NAME, Extension.class);
		project.getTasks().register(TASK_NAME, CopySync.class, (t) -> {
			t.setGroup("Help");
			t.setDescription("copy and sync dependencies as modules.");
			FileCollection from = extension.getCopySyncModules().getModules();
			if(from == null) {
				from = project.getConfigurations().findByName("runtimeClasspath");
			}
			if(from == null) {
				from = project.files();
			}
			t.setFrom(from);
			DirectoryProperty into = extension.getCopySyncModules().getInto();
			if(into.isPresent() == false) {
				into.set(project.getBuildDir().toPath().resolve("module").toFile());
			}
			t.getInto().set(into);
		});
	}

	/**
	 * 設定を表現します。
	 * @author すふぃあ
	 */
	public static class Extension {

		/** モジュールをコピーするための設定です。 */
		private CopySyncModulesExtension CopySyncModules;
		/** モジュールをコピーするための設定です。 */
		public CopySyncModulesExtension getCopySyncModules() {
			return this.CopySyncModules;
		}
		/** モジュールをコピーするための設定です。 */
		public void setCopySyncModules(CopySyncModulesExtension CopySyncModules) {
			this.CopySyncModules = CopySyncModules;
		}

		/** コンストラクタです。 */
		@Inject
		public Extension(ObjectFactory ObjectFactory) {
			this.CopySyncModules = ObjectFactory.newInstance(CopySyncModulesExtension.class);
		}

		/**
		 * モジュールをコピーするための設定を表現します。
		 * @author すふぃあ
		 */
		public static class CopySyncModulesExtension {

			/** コピー元のモジュールです。 */
			private FileCollection Modules;
			/** コピー元のモジュールです。 */
			public FileCollection getModules() { return this.Modules; }
			/** コピー元のモジュールです。 */
			public void setModules(FileCollection Modules) { this.Modules = Modules; }
			/** コピー元のモジュールです。 */
			public void Modules(FileCollection Modules) { this.setModules(Modules); }

			/** モジュールのコピー先です。 */
			private DirectoryProperty Into;
			/** モジュールのコピー先です。 */
			public DirectoryProperty getInto() { return this.Into; }
			/** モジュールのコピー先です。 */
			public void setInto(DirectoryProperty Into) { this.Into.set(Into); }
			/** モジュールのコピー先です。 */
			public void setInto(Path Into) {
				this.Into.set(Into.toFile());
			}
			/** モジュールのコピー先です。 */
			public void setInto(File Into) {
				this.Into.set(Into);
			}
			/** モジュールのコピー先です。 */
			public void setInto(String Into) {
				this.Into.set(new File(Into));
			}
			/** モジュールのコピー先です。 */
			public void into(DirectoryProperty Into) { this.setInto(Into); }
			/** モジュールのコピー先です。 */
			public void into(Path Into) { this.setInto(Into); }
			/** モジュールのコピー先です。 */
			public void into(File Into) { this.setInto(Into); }
			/** モジュールのコピー先です。 */
			public void into(String Into) { this.setInto(Into); }

			/** コンストラクタです。 */
			@Inject
			public CopySyncModulesExtension(ObjectFactory ObjectFactory) {
				DirectoryProperty directory = ObjectFactory.directoryProperty();
				this.Into = directory;
			}

		}

	}

}
