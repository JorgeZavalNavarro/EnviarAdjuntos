package com.totalplay.syntech.integracion.sdm.soainfra.wssdm.descomprimir.gz;

import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.enviar.EnviarArchivoException;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.keys.CodeKeys;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.props.AppPropsBean;
import com.totalplay.syntech.integracion.sdm.soainfra.wssdm.utils.SerialClaveBean;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Category;

/**
 *
 * @author Jorge Zavala Navarro
 */
public class DescomprimireGZBean {

    // Constantes de la clase
    private static Category log = Category.getInstance(DescomprimireGZBean.class);
    private static final int BUFFER_SIZE = 4096;

    // Propiedades de la clase
    // private String archivoTar = null;
    // private String archivoTemporal = null;
    
    public InputStream descomprimirTOInputStream(String origen, String extension) throws DescomprimireGZException {
        InputStream retorno = null;
        if (origen != null && !origen.isEmpty()) {

            // definimos el nombre de nuestro archivo destino
            String destino = origen + "." + extension;
            String tocken = (new SerialClaveBean()).getSerial(10);
            String destinoZip = origen + "_" + tocken + "." + extension + ".zip";
            int bLeidos = 0;
            try {

                this.unGunzipFile(origen, destino);

                log.info("Validando archivo zip destino");
                
                // Extremosel archivo origen
                // this.unTar(destinoZip, destino);

                // Esperamos 200 milisegundos en lo que finalizan los threads
                Thread.sleep(200);

                // Cargamos en un inputstream el contenido del archivo
                retorno = new FileInputStream(new File(destino));
                
                // Eliminamos los archivos temporales
                // log.info("Depurando..." + archivoTemporal);
                // File borrarTemp = new File(archivoTemporal);
                // borrarTemp.deleteOnExit();
                // log.info("Depurando..." + archivoTar);
                // File borrarTar = new File(archivoTar);
                // borrarTar.deleteOnExit();
                

                
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new DescomprimireGZException(ex);
            }
        } else {
            throw new DescomprimireGZException("No se está recibiendo la información completa para trabajar ");
        }
        return retorno;
    }

    @Deprecated
    private byte[] decompressGzipFileTOArrayByte(String gzipFile) throws FileNotFoundException, IOException, InterruptedException {
        byte[] result = new byte[]{};
        int len;

        // leemos el archivo
        byte[] bufferGZFile = null;
        File fileGzipFile = new File(gzipFile);
        FileInputStream fis = new FileInputStream(fileGzipFile);
        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) fileGzipFile.length());
        bufferGZFile = new byte[1024];
        while ((len = fis.read(bufferGZFile)) != -1) {
            bos.write(bufferGZFile);
            bos.flush();
        }

        // Pasamos bufferGZFile a un inputstream
        ByteArrayInputStream bis = new ByteArrayInputStream(bufferGZFile);

        return result;
    }

    public void unGunzipFile(String compressedFile, String decompressedFile) {
        byte[] buffer = new byte[1024];
        try {
            FileInputStream fileIn = new FileInputStream(compressedFile);
            GZIPInputStream gZIPInputStream = new GZIPInputStream(fileIn);
            FileOutputStream fileOutputStream = new FileOutputStream(decompressedFile);
            int bytes_read;
            while ((bytes_read = gZIPInputStream.read(buffer)) > 0) {
                fileOutputStream.write(buffer, 0, bytes_read);
            }
            gZIPInputStream.close();
            fileOutputStream.close();
            System.out.println("The file was decompressed successfully!");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = zipIn.read(bytesIn)) != -1) {
            bos.write(bytesIn, 0, read);
        }
        bos.close();
    }

    public void unzip(String zipFilePath, String filePath) throws IOException, EnviarArchivoException {

        log.info("Archivo gz --> zip: " + zipFilePath);
        ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry entry = zipIn.getNextEntry();

        // Extraemos solamente el primer archivo
        if (entry != null) {

            if (!entry.isDirectory()) {
                // if the entry is a file, extracts it
                extractFile(zipIn, filePath);
            } else {
                // if the entry is a directory, make the directory
                String error = "El archivo extraido del repositorio no es correcto.";
                throw new EnviarArchivoException(new Exception(error));
            }
            zipIn.closeEntry();
            entry = zipIn.getNextEntry();

        } else {
            String error = "No se puede extraer la información del archivo ZIP.";
            throw new EnviarArchivoException(new Exception(error));
        }
        zipIn.close();
    }

    @Deprecated
    private List<File> unTar(String tarFile, String destFile) throws FileNotFoundException, IOException, ArchiveException, InterruptedException {

        File inputFile = new File(tarFile);
        File outputDir = new File(inputFile.getParent());
        File outputFirstFile = new File(destFile);
        
        // this.archivoTar = tarFile;
        // this.archivoTemporal = destFile;

        final List<File> untaredFiles = new LinkedList<File>();
        final InputStream is = new FileInputStream(inputFile);
        final TarArchiveInputStream debInputStream = (TarArchiveInputStream) new ArchiveStreamFactory().createArchiveInputStream("tar", is);
        TarArchiveEntry entry = null;
        while ((entry = (TarArchiveEntry) debInputStream.getNextEntry()) != null) {
            final File outputFile = new File(outputDir, entry.getName());
            if (entry.isDirectory()) {
                log.info(String.format("Attempting to write output directory %s.", outputFile.getAbsolutePath()));
                if (!outputFile.exists()) {
                    log.info(String.format("Attempting to create output directory %s.", outputFile.getAbsolutePath()));
                    if (!outputFile.mkdirs()) {
                        throw new IllegalStateException(String.format("Couldn't create directory %s.", outputFile.getAbsolutePath()));
                    }
                }
            } else {
                // Extraer archivo
                log.info(String.format("Creating output file %s.", outputFirstFile));
                final OutputStream outputFileStream = new FileOutputStream(outputFirstFile);
                IOUtils.copy(debInputStream, outputFileStream);
                Thread.sleep(250);
                outputFileStream.flush();
                outputFileStream.close();
                
            }
            untaredFiles.add(outputFile);
            break;
        }
        debInputStream.close();
        is.close();
        outputFirstFile.deleteOnExit();
        
        

        return untaredFiles;
    }

    public static void main(String... params) {
        try {
            AppPropsBean.getPropsVO();
            // String archivoOrigen = "C:\\SYNTECH\\SERVICIOS\\TotalPlay\\WebServiceConsultasPDR\\Desarrollo\\IntegracionSDvsSoaInfra\\src\\java\\resources\\Ejemplos\\C25C83750386BF43BC2D9E2D2F2D4DFC_Incidente CARE.jpg.gz";
            String archivoOrigen = "C:\\Temporal\\7141C5C068B6EA4DAD1DF240DB13173F_Evidencia Resolver.png.gz";
            DescomprimireGZBean bean = new DescomprimireGZBean();
            bean.descomprimirTOInputStream(archivoOrigen, "png");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
