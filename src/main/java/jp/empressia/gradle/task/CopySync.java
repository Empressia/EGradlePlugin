package jp.empressia.gradle.task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.gradle.api.DefaultTask;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileType;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.work.FileChange;
import org.gradle.work.Incremental;
import org.gradle.work.InputChanges;

/**
 * ファイル単位でコピー同期するタスク定義です。
 * Copyタスクと異なり、同期による削除も行われます。
 * Syncタスクと異なり、ディレクトリの中のその他のファイルは削除されません（Incrementalな場合だけ）。
 * @author すふぃあ
 */
public class CopySync extends DefaultTask {

	/** コピー元のファイルです。 */
	private FileCollection From;
	/** コピー元のファイルです。 */
	@Incremental
	@InputFiles
	public FileCollection getFrom() { return this.From; }
	/** コピー元のファイルです。 */
	public void setFrom(FileCollection From) { this.From = From; }
	/** コピー元のファイルです。 */
	public void from(FileCollection From) { this.setFrom(From); }

	/** コピー先のディレクトリです。 */
	private DirectoryProperty Into;
	/** コピー先のディレクトリです。 */
	@OutputDirectory
	public DirectoryProperty getInto() { return this.Into; }
	/** コピー先のディレクトリです。 */
	public void setInto(Path Into) {
		this.getInto().set(Into.toFile());
	}
	/** コピー先のディレクトリです。 */
	public void setInto(File Into) {
		this.getInto().set(Into);
	}
	/** コピー先のディレクトリです。 */
	public void setInto(String Into) {
		this.getInto().set(new File(Into));
	}
	/** コピー先のディレクトリです。 */
	public void into(DirectoryProperty Into) { this.getInto().set(Into); }
	/** コピー先のディレクトリです。 */
	public void into(Path Into) { this.setInto(Into); }
	/** コピー先のディレクトリです。 */
	public void into(File Into) { this.setInto(Into); }
	/** コピー先のディレクトリです。 */
	public void into(String Into) { this.setInto(Into); }

	/** コンストラクタ。 */
	@Inject
	public CopySync(ObjectFactory  ObjectFactory) {
		this.Into = ObjectFactory.directoryProperty();
	}

	@TaskAction
	public void perform(InputChanges inputChanges) {
		System.out.println(MessageFormat.format("is incremental -> {0}", inputChanges.isIncremental()));
		Path intoPath = this.getInto().getAsFile().get().toPath();
		if(Files.isDirectory(intoPath) == false) {
			String message = MessageFormat.format("コピー先[{0}]にディレクトリ以外が指定されました。", intoPath);
			throw new IllegalStateException(message);
		}
		for(FileChange c : inputChanges.getFileChanges(this.getFrom())) {
			if(c.getFileType() == FileType.DIRECTORY) {
				continue;
			}
			Path fromPath = c.getFile().toPath();
			Path toPath = intoPath.resolve(fromPath.getFileName());
			if(Files.isRegularFile(fromPath) == false) {
				String message = MessageFormat.format("コピー元[{0}]にファイル以外が指定されました。", fromPath);
				throw new IllegalStateException(message);
			}
			if(Files.isDirectory(toPath)) {
				String message = MessageFormat.format("コピー先[{0}]がすでにディレクトリとして存在します。", toPath);
				throw new IllegalStateException(message);
			}
			switch(c.getChangeType()) {
				case ADDED:
				case MODIFIED: {
					try {
						Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING);
						this.getLogger().trace("ファイル[{0} -> {1}]のコピーに成功しました。", fromPath, toPath);
					} catch(IOException ex) {
						String message = MessageFormat.format("[{0} -> {1}]に失敗しました。", fromPath, toPath);
						throw new UncheckedIOException(message, ex);
					}
					break;
				}
				case REMOVED: {
					try {
						Files.deleteIfExists(toPath);
						this.getLogger().trace("ファイル[{0} -> {1}]の削除に成功しました。", fromPath, toPath);
					} catch(IOException ex) {
						String message = MessageFormat.format("[{0} -> {1}]に失敗しました。", fromPath, toPath);
						throw new UncheckedIOException(message, ex);
					}
					break;
				}
			}
		}
	}

}
