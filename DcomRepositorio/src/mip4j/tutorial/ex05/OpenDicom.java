/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mip4j.tutorial.ex05;

import ij.ImagePlus;
import ij.gui.ImageCanvas;
import ij.io.Opener;
import ij.plugin.filter.Info;

/**
 *
 * @author Rolando
 */
public class OpenDicom {

    public static void main(String[] args) {
//        ImagePlus ip = new Opener().openImage("skin.dcm");
        ImagePlus ip = new Opener().openImage("samples\\CT-MONO2-16-ankle");
        ImageCanvas ic = new ImageCanvas(ip);
        ip.show();
        Info i = new Info();
        i.setup("", ip);
        i.run(ip.getProcessor());
    }
}
