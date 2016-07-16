package com.lamfire.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	public static byte[] gzip(byte[] bs) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		GZIPOutputStream gzout = null;
		try {
			gzout = new GZIPOutputStream(bout);
			gzout.write(bs);
			gzout.flush();
		} catch (Exception e) {
			throw e;

		} finally {
			if (gzout != null) {
				try {
					gzout.close();
				} catch (Exception ex) {
				}
			}
		}

		return bout.toByteArray();

	}

	public static byte[] ungzip(byte[] bs) throws Exception {
		GZIPInputStream gzin = null;
		try {
			ByteArrayInputStream bin = new ByteArrayInputStream(bs);
			gzin = new GZIPInputStream(bin);
			return IOUtils.toByteArray(gzin);
		} catch (Exception e) {
			throw e;

		} finally {
		}

	}

    public static boolean isGzip(byte[] bs){
        return ((bs[0] == 31) && (bs[1] == -117));
    }

    public static boolean isZip(byte[] bs){
        return ((bs[0] == 120) && (bs[1] == -100));
    }

	public static byte[] zip(byte[] bs) throws Exception {

		ByteArrayOutputStream o = null;
		try {
			o = new ByteArrayOutputStream();
			Deflater compresser = new Deflater();
			compresser.setInput(bs);
			compresser.finish();
			byte[] output = new byte[1024];
			while (!compresser.finished()) {
				int got = compresser.deflate(output);
				o.write(output, 0, got);
			}
			o.flush();
			return o.toByteArray();
		} catch (Exception ex) {
			throw ex;

		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}

	}

	public static byte[] unzip(byte[] bs) throws Exception {

		ByteArrayOutputStream o = null;
		try {
			o = new ByteArrayOutputStream();
			Inflater decompresser = new Inflater();
			decompresser.setInput(bs);
			byte[] result = new byte[1024];
			while (!decompresser.finished()) {
				int resultLength = decompresser.inflate(result);
				o.write(result, 0, resultLength);
			}
			decompresser.end();
			o.flush();
			return o.toByteArray();
		} catch (Exception ex) {
			throw ex;

		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (IOException e) {
					throw e;
				}
			}
		}

	}

	public static void zip(String[] fileNames, String outZipFileName) throws Exception {
		ZipOutputStream zout = null;
		try {
			FileOutputStream fout = new FileOutputStream(outZipFileName);

			zout = new ZipOutputStream(fout);

			BufferedOutputStream out = new BufferedOutputStream(zout);
			for (int i = 0; i < fileNames.length; i++) {
				ZipEntry ze = new ZipEntry(fileNames[i]);
				BufferedInputStream in = null;
				try {
					in = new BufferedInputStream(new FileInputStream(fileNames[i]));
					zout.putNextEntry(ze);
					int readLen = 1024;
					byte[] bs = new byte[readLen];

					while (true) {
						int result = in.read(bs);
						if (result == -1)
							break;
						out.write(bs, 0, result);

					}
				} finally {
					in.close();
					out.flush();
					zout.closeEntry();
				}
			}
		} catch (Exception ex) {
			throw ex;

		} finally {
			if (zout != null)
				zout.close();
		}
	}

	/**
	 * 解压zip文件到指定的目录 File Unzip
	 * 
	 * @param sToPath
	 *            Directory path to be unzipted to
	 * @param sZipFile
	 *            zip File Name to be ziped
	 */

	public static void unzip(String sToPath, String sZipFile) throws Exception {

		if (null == sToPath || ("").equals(sToPath.trim())) {
			File objZipFile = new File(sZipFile);
			sToPath = objZipFile.getParent();
		}
		ZipFile zfile = new ZipFile(sZipFile);
		Enumeration<?> zList = zfile.entries();
		ZipEntry ze = null;
		byte[] buf = new byte[1024];
		while (zList.hasMoreElements()) {

			ze = (ZipEntry) zList.nextElement();

			if (ze.isDirectory()) {
				String dir = (sToPath + "/" + ze.getName());
				FileUtils.makeDirs(dir);

			} else {
				String filePath = (sToPath + "/" + ze.getName());
				OutputStream os = new BufferedOutputStream(new FileOutputStream(filePath));
				InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
				int readLen = 0;
				while ((readLen = is.read(buf, 0, 1024)) != -1) {
					os.write(buf, 0, readLen);
				}
				is.close();
				os.close();
			}
		}
		zfile.close();
	}

}
