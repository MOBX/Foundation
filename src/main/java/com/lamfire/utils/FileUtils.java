package com.lamfire.utils;

import java.io.*;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import com.lamfire.code.MD5;
import com.lamfire.code.SHA1;

public class FileUtils {
	public static final long ONE_KB = 1024L;
	public static final long ONE_MB = 1048576L;
	public static final long ONE_GB = 1073741824L;


	public static FileInputStream openInputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (!file.canRead())
				throw new IOException("File '" + file + "' cannot be read");
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		return new FileInputStream(file);
	}

	public static FileOutputStream openOutputStream(File file) throws IOException {
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (!file.canWrite())
				throw new IOException("File '" + file + "' cannot be written to");
		} else {
			File parent = file.getParentFile();
			if ((parent != null) && (!parent.exists()) && (!parent.mkdirs())) {
				throw new IOException("File '" + file + "' could not be created");
			}
		}

		return new FileOutputStream(file);
	}

    public static FileReader openFileReader(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead())
                throw new IOException("File '" + file + "' cannot be read");
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileReader(file);
    }

    public static FileWriter openFileWriter(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (!file.canRead())
                throw new IOException("File '" + file + "' cannot be read");
        } else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }
        return new FileWriter(file);
    }

	public static String byteCountToDisplaySize(long size) {
		String displaySize;
		if (size / 1073741824L > 0L) {
			displaySize = String.valueOf(size / 1073741824L) + " GB";
		} else {
			if (size / 1048576L > 0L) {
				displaySize = String.valueOf(size / 1048576L) + " MB";
			} else {
				if (size / 1024L > 0L)
					displaySize = String.valueOf(size / 1024L) + " KB";
				else
					displaySize = String.valueOf(size) + " bytes";
			}
		}
		return displaySize;
	}

	public static void touch(File file) throws IOException {
		if (!file.exists()) {
			OutputStream out = openOutputStream(file);
			IOUtils.closeQuietly(out);
		}
		boolean success = file.setLastModified(System.currentTimeMillis());
		if (!success)
			throw new IOException("Unable to set the last modification time for " + file);
	}

	public static File[] convertFileCollectionToFileArray(Collection<File> files) {
		return (File[]) (File[]) files.toArray(new File[files.size()]);
	}

	public static String toFilePath(URL url) {
		if ((url == null) || (!url.getProtocol().equals("file"))) {
			return null;
		}
		String filename = url.getFile().replace('/', File.separatorChar);
		int pos = 0;
		while ((pos = filename.indexOf('%', pos)) >= 0) {
			if (pos + 2 < filename.length()) {
				String hexStr = filename.substring(pos + 1, pos + 3);
				char ch = (char) Integer.parseInt(hexStr, 16);
				filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
			}
		}
		return (filename);
	}

	public static String[] toFilePaths(URL[] urls) {
		if ((urls == null) || (urls.length == 0)) {
			return null;
		}
		String[] files = new String[urls.length];
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			if (url != null) {
				if (!url.getProtocol().equals("file")) {
					throw new IllegalArgumentException("URL could not be converted to a File: " + url);
				}

				files[i] = toFilePath(url);
			}
		}
		return files;
	}

	public static void copyFileToDirectory(File srcFile, File destDir) throws IOException {
		copyFileToDirectory(srcFile, destDir, true);
	}

	public static void copyFileToDirectory(File srcFile, File destDir, boolean preserveFileDate) throws IOException {
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if ((destDir.exists()) && (!destDir.isDirectory())) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyFile(srcFile, new File(destDir, srcFile.getName()), preserveFileDate);
	}

	public static void copyFile(File srcFile, File destFile) throws IOException {
		copyFile(srcFile, destFile, true);
	}

	public static void copyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if (srcFile == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destFile == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcFile.exists()) {
			throw new FileNotFoundException("Source '" + srcFile + "' does not exist");
		}
		if (srcFile.isDirectory()) {
			throw new IOException("Source '" + srcFile + "' exists but is a directory");
		}
		if (srcFile.getCanonicalPath().equals(destFile.getCanonicalPath())) {
			throw new IOException("Source '" + srcFile + "' and destination '" + destFile + "' are the same");
		}
		if ((destFile.getParentFile() != null) && (!destFile.getParentFile().exists()) && (!destFile.getParentFile().mkdirs())) {
			throw new IOException("Destination '" + destFile + "' directory cannot be created");
		}

		if ((destFile.exists()) && (!destFile.canWrite())) {
			throw new IOException("Destination '" + destFile + "' exists but is read-only");
		}
		doCopyFile(srcFile, destFile, preserveFileDate);
	}

	private static void doCopyFile(File srcFile, File destFile, boolean preserveFileDate) throws IOException {
		if ((destFile.exists()) && (destFile.isDirectory())) {
			throw new IOException("Destination '" + destFile + "' exists but is a directory");
		}

		FileInputStream input = new FileInputStream(srcFile);
		try {
			FileOutputStream output = new FileOutputStream(destFile);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}

		if (srcFile.length() != destFile.length()) {
			throw new IOException("Failed to copy full contents from '" + srcFile + "' to '" + destFile + "'");
		}

		if (preserveFileDate)
			destFile.setLastModified(srcFile.lastModified());
	}

	public static void copyDirectoryToDirectory(File srcDir, File destDir) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if ((srcDir.exists()) && (!srcDir.isDirectory())) {
			throw new IllegalArgumentException("Source '" + destDir + "' is not a directory");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if ((destDir.exists()) && (!destDir.isDirectory())) {
			throw new IllegalArgumentException("Destination '" + destDir + "' is not a directory");
		}
		copyDirectory(srcDir, new File(destDir, srcDir.getName()), true);
	}

	public static void copyDirectory(File srcDir, File destDir) throws IOException {
		copyDirectory(srcDir, destDir, true);
	}

	public static void copyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
		if (srcDir == null) {
			throw new NullPointerException("Source must not be null");
		}
		if (destDir == null) {
			throw new NullPointerException("Destination must not be null");
		}
		if (!srcDir.exists()) {
			throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
		}
		if (!srcDir.isDirectory()) {
			throw new IOException("Source '" + srcDir + "' exists but is not a directory");
		}
		if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
			throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
		}
		doCopyDirectory(srcDir, destDir, preserveFileDate);
	}

	private static void doCopyDirectory(File srcDir, File destDir, boolean preserveFileDate) throws IOException {
		if (destDir.exists()) {
			if (!destDir.isDirectory())
				throw new IOException("Destination '" + destDir + "' exists but is not a directory");
		} else {
			if (!destDir.mkdirs()) {
				throw new IOException("Destination '" + destDir + "' directory cannot be created");
			}
			if (preserveFileDate) {
				destDir.setLastModified(srcDir.lastModified());
			}
		}
		if (!destDir.canWrite()) {
			throw new IOException("Destination '" + destDir + "' cannot be written to");
		}

		File[] files = srcDir.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of " + srcDir);
		}
		for (int i = 0; i < files.length; i++) {
			File copiedFile = new File(destDir, files[i].getName());
			if (files[i].isDirectory())
				doCopyDirectory(files[i], copiedFile, preserveFileDate);
			else
				doCopyFile(files[i], copiedFile, preserveFileDate);
		}
	}

	public static void copyURLToFile(URL source, File destination) throws IOException {
		InputStream input = source.openStream();
		try {
			FileOutputStream output = openOutputStream(destination);
			try {
				IOUtils.copy(input, output);
			} finally {
				IOUtils.closeQuietly(output);
			}
		} finally {
			IOUtils.closeQuietly(input);
		}
	}

	public static void deleteDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectory(directory);
		if (!directory.delete()) {
			String message = "Unable to delete directory " + directory + ".";

			throw new IOException(message);
		}
	}

	public static void cleanDirectory(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDelete(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception)
			throw exception;
	}

	public static String readFileToString(File file, String encoding) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			String str = IOUtils.toString(in, encoding);
			return str;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static String readFileToString(File file) throws IOException {
		return readFileToString(file, null);
	}

	public static byte[] readFileToByteArray(File file) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			byte[] arrayOfByte = IOUtils.toByteArray(in);
			return arrayOfByte;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static List<String> readLines(File file, String encoding) throws IOException {
		InputStream in = null;
		try {
			in = openInputStream(file);
			List<String> localList = IOUtils.readLines(in, encoding);
			return localList;
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	public static List<String> readLines(File file) throws IOException {
		return readLines(file, null);
	}

	public static void writeStringToFile(File file, String data, String encoding) throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			IOUtils.write(out,data,encoding);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static void writeStringToFile(File file, String data) throws IOException {
		writeStringToFile(file, data, null);
	}

	public static void writeByteArrayToFile(File file, byte[] data) throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			out.write(data);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static void writeLines(File file, String encoding, Collection<String> lines) throws IOException {
		writeLines(file, encoding, lines, null);
	}

	public static void writeLines(File file, Collection<String> lines) throws IOException {
		writeLines(file, null, lines, null);
	}

	public static void writeLines(File file, String encoding, Collection<String> lines, String lineEnding) throws IOException {
		OutputStream out = null;
		try {
			out = openOutputStream(file);
			IOUtils.writeLines(out,lines, lineEnding,   encoding);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	public static void writeLines(File file, Collection<String> lines, String lineEnding) throws IOException {
		writeLines(file, null, lines, lineEnding);
	}

	public static void forceDelete(File file) throws IOException {
		if (file.isDirectory()) {
			deleteDirectory(file);
		} else {
			if (!file.exists()) {
				throw new FileNotFoundException("File does not exist: " + file);
			}
			if (!file.getAbsoluteFile().delete()) {
				String message = "Unable to delete file: " + file;

				throw new IOException(message);
			}
		}
	}

	public static void forceDeleteOnExit(File file) throws IOException {
		if (file.isDirectory())
			deleteDirectoryOnExit(file);
		else
			file.getAbsoluteFile().deleteOnExit();
	}

	private static void deleteDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) {
			return;
		}

		cleanDirectoryOnExit(directory);
		directory.deleteOnExit();
	}

	private static void cleanDirectoryOnExit(File directory) throws IOException {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		File[] files = directory.listFiles();
		if (files == null) {
			throw new IOException("Failed to list contents of " + directory);
		}

		IOException exception = null;
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				forceDeleteOnExit(file);
			} catch (IOException ioe) {
				exception = ioe;
			}
		}

		if (null != exception)
			throw exception;
	}

	public static void forceMkdir(File directory) throws IOException {
		if (directory.exists()) {
			if (directory.isFile()) {
				String message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";

				throw new IOException(message);
			}
		} else if (!directory.mkdirs()) {
			String message = "Unable to create directory " + directory;

			throw new IOException(message);
		}
	}

	public static long sizeOfDirectory(File directory) {
		if (!directory.exists()) {
			String message = directory + " does not exist";
			throw new IllegalArgumentException(message);
		}

		if (!directory.isDirectory()) {
			String message = directory + " is not a directory";
			throw new IllegalArgumentException(message);
		}

		long size = 0L;

		File[] files = directory.listFiles();
		if (files == null) {
			return 0L;
		}
		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.isDirectory())
				size += sizeOfDirectory(file);
			else {
				size += file.length();
			}
		}

		return size;
	}

	public static boolean isFileNewer(File file, File reference) {
		if (reference == null) {
			throw new IllegalArgumentException("No specified reference file");
		}
		if (!reference.exists()) {
			throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
		}

		return isFileNewer(file, reference.lastModified());
	}

	public static boolean isFileNewer(File file, Date date) {
		if (date == null) {
			throw new IllegalArgumentException("No specified date");
		}
		return isFileNewer(file, date.getTime());
	}

	public static boolean isFileNewer(File file, long timeMillis) {
		if (file == null) {
			throw new IllegalArgumentException("No specified file");
		}
		if (!file.exists()) {
			return false;
		}
		return file.lastModified() > timeMillis;
	}

	public static boolean isFileOlder(File file, File reference) {
		if (reference == null) {
			throw new IllegalArgumentException("No specified reference file");
		}
		if (!reference.exists()) {
			throw new IllegalArgumentException("The reference file '" + file + "' doesn't exist");
		}

		return isFileOlder(file, reference.lastModified());
	}

	public static boolean isFileOlder(File file, Date date) {
		if (date == null) {
			throw new IllegalArgumentException("No specified date");
		}
		return isFileOlder(file, date.getTime());
	}

	public static boolean isFileOlder(File file, long timeMillis) {
		if (file == null) {
			throw new IllegalArgumentException("No specified file");
		}
		if (!file.exists()) {
			return false;
		}
		return file.lastModified() < timeMillis;
	}

	public static long checksumCRC32(File file) throws IOException {
		CRC32 crc = new CRC32();
		checksum(file, crc);
		return crc.getValue();
	}
	
	public static String checksumMD5(File file) throws IOException {
		return MD5.hash(file);
	}
	
	public static String checksumSHA1(File file) throws IOException {
		return SHA1.hash(file);
	}

	public static Checksum checksum(File file, Checksum checksum) throws IOException {
		if (file.isDirectory()) {
			throw new IllegalArgumentException("Checksums can't be computed on directories");
		}
		InputStream in = null;
		try {
			in = new CheckedInputStream(new FileInputStream(file), checksum);
			IOUtils.copy(in, new NullOutputStream());
		} finally {
			IOUtils.closeQuietly(in);
		}
		return checksum;
	}

	
	public static boolean exists(String file){
		return exists(new File(file));
	}

    public static boolean hasParentDirs(String file){
        return hasParentDirs(new File(file));
    }
	
	public static boolean existsParentDirs(String file){
		return existsParentDirs(new File(file));
	}
	
	public static boolean exists(File file){
		return file.exists();
	}
	
	public static void makeDirs(String dir) {
		File file = new File(dir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

    public static boolean hasParentDirs(File file){
        try{
            File parent = file.getCanonicalFile().getParentFile();
            if (parent != null) {
                return true;
            }
        }catch(IOException e){

        }
        return false;
    }
	
	public static boolean existsParentDirs(File file){
		try{
			File parent = file.getCanonicalFile().getParentFile();
			if (parent == null) {
				return false;
			}
			if (parent.isDirectory() && parent.exists()){
				return true;
			}
		}catch(IOException e){
			
		}
		return false;
	}

	public static void makeParentDirs(File file) throws IOException {
		makeParentDirsIfNotExists(file);
	}

    public static void makeParentDirsIfNotExists(File file) throws IOException {
        File parent = file.getCanonicalFile().getParentFile();
        if (parent == null) {
            return;
        }
        if(!parent.exists()){
            parent.mkdirs();
        }
        if (!parent.isDirectory()){
            throw new IOException("Unable to create parent directories of " + file);
        }
    }

	static class NullOutputStream extends OutputStream {
		public void write(byte[] b, int off, int len) {
		}

		public void write(int b) {
		}

		public void write(byte[] b) throws IOException {
		}
	}
}