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
package mip4j.tutorial.ex01;

import ij.ImagePlus;
import ij.io.Opener;
import ij.plugin.filter.Info;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import mod.lu.tudor.santec.dicom.exporter.DicomExporter;

import org.dcm4che2.data.DicomObject;

// First DICOM Object
public class DICOMize {

    public static void main(String[] args) throws IOException {

        BufferedImage bi = ImageIO.read(DICOMize.class.getResourceAsStream("../resources/skin.jpg"));
        ImagePlus imp = new ImagePlus("", bi);

        DicomExporter exporter = new DicomExporter();
        DicomObject dObj = exporter.createHeader(imp, true, true, true);

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
