package com.harvie.plugin

import java.nio.channels.FileChannel

/**
 * author harvie
 * 文件操作工具类
 */
class FileUtils{

    static boolean copyFile(File source, File dest){
        if (dest.isFile() && dest.exists()){
            dest.delete()
            dest.createNewFile()
        }
        FileChannel ins =  new FileInputStream(source).getChannel()
        FileChannel ops = new FileOutputStream(dest).getChannel()
        try {
            ops.transferFrom(ins,0,ins.size())
        }catch(Exception e){
            e.printStackTrace()
        }finally{
            ins.close()
            ops.close()
        }
    }

    static boolean copyDirectory(File srcDir, File destDir)throws IOException{
        if (srcDir == null) {
            throw new NullPointerException("Source must not be null");
        } else if (destDir == null) {
            throw new NullPointerException("Destination must not be null");
        } else if (!srcDir.exists()) {
            throw new FileNotFoundException("Source '" + srcDir + "' does not exist");
        } else if (!srcDir.isDirectory()) {
            throw new IOException("Source '" + srcDir + "' exists but is not a directory");
        } else if (srcDir.getCanonicalPath().equals(destDir.getCanonicalPath())) {
            throw new IOException("Source '" + srcDir + "' and destination '" + destDir + "' are the same");
        } else {
            List<String> exclusionList = null;
            if (destDir.getCanonicalPath().startsWith(srcDir.getCanonicalPath())) {
                File[] srcFiles = srcDir.listFiles()
                if (srcFiles != null && srcFiles.length > 0) {
                    exclusionList = new ArrayList(srcFiles.length)
                    File[] arr$ = srcFiles
                    int len$ = srcFiles.length

                    for(int i$ = 0; i$ < len$; ++i$) {
                        File srcFile = arr$[i$]
                        File copiedFile = new File(destDir, srcFile.getName())
                        exclusionList.add(copiedFile.getCanonicalPath())
                    }
                }
            }

            doCopyDirectory(srcDir, destDir, exclusionList)
        }
    }

    private static void doCopyDirectory(File srcDir, File destDir, List<String> exclusionList) throws IOException {
        File[] srcFiles = srcDir.listFiles()
        if (srcFiles == null) {
            throw new IOException("Failed to list contents of " + srcDir)
        } else {
            if (destDir.exists()) {
                if (!destDir.isDirectory()) {
                    throw new IOException("Destination '" + destDir + "' exists but is not a directory");
                }
            } else if (!destDir.mkdirs() && !destDir.isDirectory()) {
                throw new IOException("Destination '" + destDir + "' directory cannot be created");
            }

            if (!destDir.canWrite()) {
                throw new IOException("Destination '" + destDir + "' cannot be written to");
            } else {
                File[] arr$ = srcFiles;
                int len$ = srcFiles.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    File srcFile = arr$[i$]
                    File dstFile = new File(destDir, srcFile.getName());
                    if (exclusionList == null || !exclusionList.contains(srcFile.getCanonicalPath())) {
                        if (srcFile.isDirectory()) {
                            doCopyDirectory(srcFile, dstFile, exclusionList);
                        } else {
                            copyFile(srcFile, dstFile)
                        }
                    }
                }

            }
        }
    }

    static byte[] file2byte(File file)
    {
        byte[] buffer = null
        try
        {
            FileInputStream fis = new FileInputStream(file)
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
            byte[] b = new byte[1024]
            int n
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n)
            }
            fis.close()
            bos.close()
            buffer = bos.toByteArray()
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace()
        }
        catch (IOException e)
        {
            e.printStackTrace()
        }
        return buffer
    }

    static byte[] file2byte(InputStream inputStream){
        byte[] buffer = null
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream()
            byte[] b = new byte[1024]
            int n
            while ((n = inputStream.read(b)) != -1){
                bos.write(b, 0, n)
            }
            inputStream.close()
            bos.close()
            buffer = bos.toByteArray()
        }catch (FileNotFoundException e){
            e.printStackTrace()
        }catch (IOException e){
            e.printStackTrace()
        }
        return buffer
    }

    static void byte2File(byte[] buf, String filePath1, String fileName1){
        BufferedOutputStream bos = null
        FileOutputStream fos = null
        File file = null
        try{
            //win和linux环境下\/不同，需要转一次
            String filePath = filePath1.replace('/',File.separator)
            String fileName = fileName1.replace('/','').replace(File.separator,'')
            File dir = new File(filePath)
            if (!dir.exists() && dir.isDirectory()){
                dir.mkdirs()
            }
            file = new File(filePath,fileName)
//            HLog.i("byte2File:"+file.absolutePath)
            if (!file.exists()){
                file.createNewFile()
            }
            fos = new FileOutputStream(file)
            bos = new BufferedOutputStream(fos)
            bos.write(buf)
            bos.flush()
        }catch (Exception e){
            e.printStackTrace()
        }finally{
            try{
                if (bos!=null){
                    bos.close()
                }
                if (fos!=null){
                    fos.close()
                }
            }catch (Exception e){
                e.printStackTrace()
            }
        }
    }
}