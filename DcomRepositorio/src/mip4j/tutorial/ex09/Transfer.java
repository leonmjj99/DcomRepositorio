/**
 * *****************************************************************************
 * Copyright (c) 2014 Ju Ou-Yang (juouyang@gmail.com). All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the GNU Lesser Public License v2.1 which accompanies this distribution, and
 * is available at http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors: Ju Ou-Yang (juouyang@gmail.com) - initial API and
 * implementation
 *****************************************************************************
 */
package mip4j.tutorial.ex09;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.filter.Info;

import java.io.IOException;
import java.util.List;

import mod.org.dcm4che2.tool.DcmQR;
import mod.org.dcm4che2.tool.DcmQR.QueryRetrieveLevel;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.net.ConfigurationException;

// C-MOVE
// dcmqr -L DCMRCV:11106 CONQUESTSRV1@localhost:5678 -cmove DCMRCV -qPatientName=HEAD* -cstore CT -cstoredest R:/tmp
public class Transfer {

    public static void main(String[] args) throws IOException, ConfigurationException, InterruptedException {
		// JVSDICOM localhost 104
        // DCM4CHEE localhost 11112
        // CONQUESTSRV1 localhost 5678
        final String callingAET = "MIP4J";
        final String calledAET = "JVSDICOM";
        final String remoteHost = "127.0.0.1";
        final int remotePort = 104;

        String pID = "A123456789";
        String modality = "SC";
        String destDir = "R:/tmp";

        DcmQR dcmqr = new DcmQR(callingAET);
        dcmqr.setCalledAET(calledAET, false);
        dcmqr.setRemoteHost(remoteHost);
        dcmqr.setRemotePort(remotePort);
        dcmqr.setCalling("DCMRCV"); // Setup this AE in PACS Server
        dcmqr.setLocalPort(11106); // Setup this AE in PACS Server
        dcmqr.setPackPDV(true);
        dcmqr.setTcpNoDelay(true);
        dcmqr.setMaxOpsInvoked(1);
        dcmqr.setMaxOpsPerformed(0);
        dcmqr.addStoreTransferCapability(DcmQR.CUID.valueOf(modality).uid, DcmQR.DEF_TS);
        dcmqr.setStoreDestination(destDir);
        dcmqr.setCFind(true);
        dcmqr.setCGet(false);
        dcmqr.setMoveDest("DCMRCV");
        dcmqr.setQueryLevel(QueryRetrieveLevel.IMAGE);
        dcmqr.addDefReturnKeys();
        dcmqr.configureTransferCapability(false);
        dcmqr.addMatchingKey(Tag.toTagPath("PatientID"), pID);

        dcmqr.start();
        dcmqr.open();
        List<DicomObject> result;
        if (dcmqr.isCFind()) {
            result = dcmqr.query();

            for (DicomObject dcmObj : result) {
                System.out.println(dcmObj);
            }

            if (dcmqr.isCMove()) {
                dcmqr.move(result);

                for (DicomObject dcmObj : result) {
                    System.out.println(dcmObj);
                    ImagePlus imp = new Opener().openImage(destDir + "/" + dcmObj.getString(Tag.SOPInstanceUID));
                    imp.show();
                    Info i = new Info();
                    i.setup("", imp);
                    i.run(imp.getProcessor());
                }
            }
        }
        dcmqr.close();
        dcmqr.stop();
    }

}
