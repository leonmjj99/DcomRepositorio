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
package mip4j.tutorial.ex04;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.filter.Info;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import mod.lu.tudor.santec.dicom.exporter.DicomExporter;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;

// UTF-8 Support
public class DICOMize {

    public static void main(String[] args) throws IOException, ParseException {
        BufferedImage bi = ImageIO.read(DICOMize.class.getResourceAsStream("../resources/skin.jpg"));
        ImagePlus imp = new ImagePlus("", bi);

        DicomExporter exporter = new DicomExporter();
        DicomObject dObj = exporter.createHeader(imp, true, true, true);

        // SOP Class
        String studyID = "9527";
        String studyInstanceUID = "1.2.840.1.2035.84778572." + studyID;
        int SeriesNumber = 1;
        String sopInstanceUID = studyInstanceUID + "." + SeriesNumber + "." + 1;
        String sopClass = UID.SecondaryCaptureImageStorage;

        dObj.putString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
        dObj.putString(Tag.MediaStorageSOPInstanceUID, VR.UI, sopInstanceUID);
        dObj.putString(Tag.SOPClassUID, VR.UI, sopClass);
        dObj.putString(Tag.SOPInstanceUID, VR.UI, sopInstanceUID);
        dObj.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 192"); // UTF8
        dObj.putString(Tag.TransferSyntaxUID, VR.UI, TransferSyntax.ImplicitVRLittleEndian.uid());
        // Patient
        dObj.putString(Tag.PatientName, VR.PN, "欧阳儒");
        dObj.putString(Tag.PatientID, VR.LO, "A123456789");
        DateFormat df = DateFormat.getDateInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("15/04/2015");
        dObj.putDate(Tag.PatientBirthDate, VR.DA, date);
        dObj.putString(Tag.PatientSex, VR.CS, "M");
        // Study
        dObj.putString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID);
        Date now = new Date();
        dObj.putDate(Tag.StudyDate, VR.DA, now);
        dObj.putDate(Tag.StudyTime, VR.TM, now);
        dObj.putString(Tag.ReferringPhysicianName, VR.PN, "黄柏荣医生");
        dObj.putString(Tag.StudyID, VR.SH, studyID);
        // Series
        dObj.putString(Tag.Modality, VR.CS, "CS");
        dObj.putString(Tag.SeriesInstanceUID, VR.UI, studyInstanceUID + "." + SeriesNumber);
        dObj.putInt(Tag.SeriesNumber, VR.IS, SeriesNumber);
        // Image
        dObj.putInt(Tag.InstanceNumber, VR.IS, 1);
        dObj.putDate(Tag.InstanceCreationDate, VR.DA, now);
        dObj.putDate(Tag.InstanceCreationTime, VR.TM, now);
        // Pixel Data
        int colorComponents = bi.getColorModel().getNumColorComponents();
        int bitsPerPixel = bi.getColorModel().getPixelSize();
        int bitsAllocated = (bitsPerPixel / colorComponents);
        int samplesPerPixel = colorComponents;
        dObj.putString(Tag.PhotometricInterpretation, VR.CS, "RGB");
        dObj.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
        dObj.putInt(Tag.Rows, VR.US, bi.getHeight());
        dObj.putInt(Tag.Columns, VR.US, bi.getWidth());
        dObj.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
        dObj.putInt(Tag.BitsStored, VR.US, bitsAllocated);
        dObj.putInt(Tag.HighBit, VR.US, bitsAllocated - 1);
        dObj.putInt(Tag.PixelRepresentation, VR.US, 0);
        dObj.putInt(Tag.PixelPaddingValue, VR.US, 0);

        double max = imp.getDisplayRangeMax();
        double min = imp.getDisplayRangeMin();
        dObj.putDouble(Tag.WindowCenter, VR.DS, min + (max - min) / 2);
        dObj.putDouble(Tag.WindowWidth, VR.DS, (max - min));

        File out = new File("skin.dcm");
        exporter.write(dObj, imp, out, false);

        System.out.println(dObj);
        imp = new Opener().openImage(out.getAbsolutePath());
        imp.show();
        Info i = new Info();
        i.setup("", imp);
        i.run(imp.getProcessor());
    }
}
