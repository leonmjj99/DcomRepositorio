package mip4j.tutorial.ex10;

import ij.ImageJ;
import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.FolderOpener;
import ij.plugin.ZProjector;
import ij.plugin.filter.Info;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import mod.lu.tudor.santec.dicom.exporter.DicomExporter;

import org.dcm4che2.data.DicomObject;
import org.dcm4che2.data.Tag;
import org.dcm4che2.data.TransferSyntax;
import org.dcm4che2.data.UID;
import org.dcm4che2.data.VR;

public class Main {

    public static void main(String[] args) throws IOException, ParseException {
        ImageJ ij = new ImageJ();
        ij.exitWhenQuitting(true);

        ZProjector z = new ZProjector(new FolderOpener().openFolder(Main.class.getResource("../resources/mr/").getPath()));
        z.setMethod(1);
        z.doProjection();
        ImagePlus mip = z.getProjection();
        mip.getProcessor().flipVertical();

        DicomExporter exporter = new DicomExporter();
        DicomObject dObj = exporter.createHeader(mip, false, false, false);

        // SOP Class
        String studyID = "9527";
        String studyInstanceUID = "1.2.840.1.2035.84778572." + studyID;
        int SeriesNumber = 2;
        String sopInstanceUID = studyInstanceUID + "." + SeriesNumber + "." + 1;
        String sopClass = UID.SecondaryCaptureImageStorage;

        dObj.putString(Tag.MediaStorageSOPClassUID, VR.UI, sopClass);
        dObj.putString(Tag.MediaStorageSOPInstanceUID, VR.UI, sopInstanceUID);
        dObj.putString(Tag.SOPClassUID, VR.UI, sopClass);
        dObj.putString(Tag.SOPInstanceUID, VR.UI, sopInstanceUID);
        dObj.putString(Tag.SpecificCharacterSet, VR.CS, "ISO_IR 100");
        dObj.putString(Tag.TransferSyntaxUID, VR.UI, TransferSyntax.ImplicitVRLittleEndian.uid());
        // Patient
        dObj.putString(Tag.PatientName, VR.PN, "Ju Ou-Yang");
        dObj.putString(Tag.PatientID, VR.LO, "A123456789");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date = sdf.parse("15/04/2015");
        dObj.putDate(Tag.PatientBirthDate, VR.DA, date);
        dObj.putString(Tag.PatientSex, VR.CS, "M");
        // Study
        dObj.putString(Tag.StudyInstanceUID, VR.UI, studyInstanceUID);
        Date now = new Date();
        dObj.putDate(Tag.StudyDate, VR.DA, now);
        dObj.putDate(Tag.StudyTime, VR.TM, now);
        dObj.putString(Tag.ReferringPhysicianName, VR.PN, "Dr. Who");
        dObj.putString(Tag.StudyID, VR.SH, studyID);
        // Series
        dObj.putString(Tag.Modality, VR.CS, "SC");
        dObj.putString(Tag.SeriesInstanceUID, VR.UI, studyInstanceUID + "." + SeriesNumber);
        dObj.putInt(Tag.SeriesNumber, VR.IS, SeriesNumber);
        // Image
        dObj.putInt(Tag.InstanceNumber, VR.IS, 1);
        dObj.putDate(Tag.InstanceCreationDate, VR.DA, now);
        dObj.putDate(Tag.InstanceCreationTime, VR.TM, now);
        // Pixel Data
        int samplesPerPixel = mip.getBytesPerPixel() * 8 / mip.getBitDepth();
        int bitsAllocated = mip.getBitDepth();
        String photometricInterpretation = samplesPerPixel == 3 ? "YBR_FULL_422" : "MONOCHROME2"; // RGB?
        dObj.putString(Tag.PhotometricInterpretation, VR.CS, photometricInterpretation);
        dObj.putInt(Tag.SamplesPerPixel, VR.US, samplesPerPixel);
        dObj.putInt(Tag.Rows, VR.US, mip.getHeight());
        dObj.putInt(Tag.Columns, VR.US, mip.getWidth());
        dObj.putInt(Tag.BitsAllocated, VR.US, bitsAllocated);
        dObj.putInt(Tag.BitsStored, VR.US, bitsAllocated);
        dObj.putInt(Tag.HighBit, VR.US, bitsAllocated - 1);
        dObj.putInt(Tag.PixelRepresentation, VR.US, 0);
        dObj.putInt(Tag.PixelPaddingValue, VR.US, 0);

        double max = mip.getDisplayRangeMax();
        double min = mip.getDisplayRangeMin();
        dObj.putDouble(Tag.WindowCenter, VR.DS, min + (max - min) / 2);
        dObj.putDouble(Tag.WindowWidth, VR.DS, (max - min));

        File out = new File("mip.dcm");
        exporter.write(dObj, mip, out, false);

        // open it
        System.out.println(dObj);
        ImagePlus imp = new Opener().openImage(out.getAbsolutePath());
        imp.show();
        Info i = new Info();
        i.setup("", imp);
        i.run(imp.getProcessor());
    }

}
