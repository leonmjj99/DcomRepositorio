package mip4j.tutorial.ex06;

import java.io.File;
import java.io.IOException;

import mod.org.dcm4che2.tool.DcmSnd;

import org.dcm4che2.net.ConfigurationException;

public class Send {

    public static void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		// JVSDICOM localhost 104
        // DCM4CHEE localhost 11112
        // CONQUESTSRV1 localhost 5678
        final String callingAET = "MIP4J";
        final String calledAET = "JVSDICOM";
        final String remoteHost = "127.0.0.1";
        final int remotePort = 104;

        DcmSnd dcmsnd = new DcmSnd(callingAET);
        dcmsnd.setCalledAET(calledAET);
        dcmsnd.setRemoteHost(remoteHost);
        dcmsnd.setRemotePort(remotePort);
        dcmsnd.setOfferDefaultTransferSyntaxInSeparatePresentationContext(false);
        dcmsnd.setSendFileRef(false);
        dcmsnd.setStorageCommitment(false);
        dcmsnd.setPackPDV(true);
        dcmsnd.setTcpNoDelay(true);

        System.out.println("Scanning files to send");
        String path = "./skin.dcm";
		// String path = Main.class.getResource("../resources/mr/").getPath();
        // String path = "./mip.dcm";
        dcmsnd.addFile(new File(path));
        System.out.println("\nScanned " + dcmsnd.getNumberOfFilesToSend() + " files");
        dcmsnd.configureTransferCapability();
        while (dcmsnd.getLastSentFile() < dcmsnd.getNumberOfFilesToSend()) {
            dcmsnd.start();
            dcmsnd.open();
            dcmsnd.send();
            dcmsnd.close();
            dcmsnd.stop();
        }
    }

}
