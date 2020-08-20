/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omics.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class cAnnovar {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static String folder = "/home/yqzhao/NetBeansProjects/cAnnovar/web/";

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        String keyspace = "annovar";
        String[] connectionParameters = {"localhost", "9042", keyspace};

        String table = "geneStat";

        String cytobandFile = folder + "humandb/hg19_cytoBand.txt";
        String refGene = folder + "humandb/hg19_refGene.txt";
        String refGeneMrna = folder + "humandb/hg19_refGeneMrna.fa";
        String testVCF = folder + "data/84060.vcf";

        //Annotation
//        Annotation an = new Annotation(connectionParameters, table, cytobandFile);
//	      an.uploadCytoband(cytobandFile);
//	      an.uploadRefGene(refGene);
//	      an.initCytoband(cytobandFile);
//	      an.uploadRefGeneMrna(refGene, refGeneMrna);
//	      an.filterCassandraRefGene(refGene, refGeneMrna);
//		  Annotation an = new Annotation();
//        
//        an.initCytoband(cytobandFile);
//        an.initRefGene(refGene, refGeneMrna);
//        an.geneStats();
//        //remove ref gene startsWith("NR")
//        an.filterTransctipts();
//        an.geneStats();
//
//        String annotatedVCFout1 = "/home/yqzhao/NetBeansProjects/cAnnovar/web/data/84060.vcf.annotated.txt";
//        annotateVCF(testVCF, an, annotatedVCFout1);
//        an.closeConnection();
		
        //Annotation - from Cassandra
        Annotation2 an2 = new Annotation2(connectionParameters, table, cytobandFile);
//      String annotatedVCFout2 = "/home/yqzhao/NetBeansProjects/cAnnovar/web/data/84060.vcf.annotated2.txt";
		annotateVCF2(testVCF, an2, annotatedVCFout2);
//   	long startTime = System.currentTimeMillis();
        System.out.println(an2.annotateVariant2_1(newTests1));
//      long endTime   = System.currentTimeMillis();
//      long totalTime = endTime - startTime;
//      System.out.println("Tol"+totalTime);
        an2.closeConnection();
//	    an2.initCytoband(cytobandFile);
//	    an2.initRefGene(refGene, refGeneMrna);
//	    an2.filterTransctipts();  

//        ArrayList<String> variant1 = an.annotateVariant(Integer.parseInt(testVariant1[0]),testVariant1[1],Integer.parseInt(testVariant1[2]),Integer.parseInt(testVariant1[3]),testVariant1[4],testVariant1[5]);
//        
//        ArrayList<String> variant2 = an.annotateVariant(Integer.parseInt(testVariant2[0]),testVariant2[1],Integer.parseInt(testVariant2[2]),Integer.parseInt(testVariant2[3]),testVariant2[4],testVariant2[5]);
//        
//        System.out.println("variant1");
//        
//        printArrayList(variant1);
//        
//        System.out.println("variant2");
//        
//        printArrayList(variant2);
//        
        System.exit(0);

    }

    public static ArrayList<String> getVCFHeader(String _arg) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {

        ArrayList<String> header = new ArrayList<>();

        try (BufferedReader brHeader = new BufferedReader(new FileReader(_arg))) {
            String line;

            while ((line = brHeader.readLine()) != null) {

                if (line.startsWith("#CHROM")) {
                    header.add(line.trim());
                    break;
                } else {
                    header.add(line.trim());
                }

            }
        }

        return header;
    }

    public static ArrayList<String> parseVCFtoAvinput(String _arg, Annotation _an) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {

        ArrayList<String> avinput = new ArrayList<>();

        try (BufferedReader brHeader = new BufferedReader(new FileReader(_arg))) {
            String line;

            while ((line = brHeader.readLine()) != null) {

                if (!line.startsWith("#")) {

                    //System.out.println(line);
                    String[] fields = line.trim().split("\t");

                    int GT = -1;
                    int DP = -1;
                    String QUAL = fields[5];

                    String genotype;

                    String[] format = fields[8].trim().split(":");

                    for (int i = 0; i < format.length; i++) {
                        if (format[i].equals("GT")) {
                            GT = i;
                        }
                        if (format[i].equals("DP")) {
                            DP = i;
                        }
                    }

                    String[] subject = fields[9].trim().split(":");

                    if (subject[GT].contains(".")) {
                        continue;
                    } else if (subject[GT].contains("2")) {
                        genotype = "het";
                    } else if (!subject[GT].contains("1")) {
                        genotype = "hom";
                    } else if (!subject[GT].contains("0")) {
                        genotype = "hom";
                    } else {
                        genotype = "het";
                    }

                    if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.]+$")) {

                        int chrom = Cytoband.getIntChrom(fields[0]);

                        String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(), fields[4].trim());

                        avinput.add((chrom + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3] + "\t" + genotype + "\t" + QUAL + "\t" + subject[DP]));

                    } else if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.,]+$")) {
                        String[] altAlt = fields[4].trim().split(",");
                        for (String alt : altAlt) {
                            //System.out.println(line);
                            int chrom = Cytoband.getIntChrom(fields[0]);

                            String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(), alt);

                            avinput.add((chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0])) + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + alt + "\t" + genotype + "\t" + QUAL + "\t" + subject[DP]));

                        }
                    }

                }

            }
        }

        return avinput;

    }

    public static void annotateVCF(String _inputVCF, Annotation _an, String _outputFile) throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
//         try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
//            for (String str : _list) {
//                writer.println(str);
//
//                writer.flush();
//            }
//            writer.close();
//        }

        System.out.println("Annotating vcf " + _inputVCF);

        try (BufferedReader brHeader = new BufferedReader(new FileReader(_inputVCF))) {
            String line;

            try (PrintWriter writer = new PrintWriter(_outputFile, "UTF-8")) {
                int currentChrom = -1;

                writer.println(_an.getVariantHeaders());

                while ((line = brHeader.readLine()) != null) {

                    if (!line.startsWith("#")) {

                        //System.out.println(line);
                        String[] fields = line.trim().split("\t");

                        //Get genotype
                        int GT = -1;
                        int DP = -1;
                        String QUAL = fields[5];

                        String genotype;

                        String[] format = fields[8].trim().split(":");

                        for (int i = 0; i < format.length; i++) {
                            if (format[i].equals("GT")) {
                                GT = i;
                            }
                            if (format[i].equals("DP")) {
                                DP = i;
                            }
                        }

                        String[] subject = fields[9].trim().split(":");

                        if (subject[GT].contains(".")) {
                            continue;
                        } else if (subject[GT].contains("2")) {
                            genotype = "het";
                        } else if (!subject[GT].contains("1")) {
                            genotype = "hom";
                        } else if (!subject[GT].contains("0")) {
                            genotype = "hom";
                        } else {
                            genotype = "het";
                        }

                        if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.]+$")) {

                            int chrom = Cytoband.getIntChrom(fields[0]);

                            if (currentChrom != chrom) {
                                System.out.println("Processing Chromosome " + chrom);
                                currentChrom = chrom;
                            }

                            String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(), fields[4].trim());
                            String variant = "";
                            if (chrom == 0) {
                                // e.g. chr1_gl000191_random
                                variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
                            } else // chr1-23+X+Y+M+MT
                            {
                                variant = chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0])) + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
                            }

                            writer.println(_an.annotateVariant(variant));

                            //avinput.add((chrom + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3] + "\t" + genotype + "\t" + QUAL + "\t" + subject[DP] ));
                        }
//                        else if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.,]+$")) {
//                            String[] altAlt = fields[4].trim().split(",");
//                            for (String alt : altAlt) {
//                                //System.out.println(line);
//                                int chrom = Cytoband.getIntChrom(fields[0]);
//
//                                if (currentChrom != chrom) {
//                                    System.out.println("Processing Chromosome " + currentChrom);
//                                    currentChrom = chrom;
//                                }
//
//                                String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(), alt);
//
//                                String variant = chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0])) + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
//                                writer.println(_an.annotateVariant(variant));
//
//                                //avinput.add((chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0]))+ "\t" +startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" + genotype + "\t" + QUAL + "\t" + subject[DP]));
//                            }
//                        }

                    }

                    writer.flush();

                }
            }

        }

    }

    // Select from cassandra||betweenTx&Upstream&Downstream
    public static void annotateVCF2(String _inputVCF, Annotation _an, String _outputFile)
            throws FileNotFoundException, IOException, InterruptedException, ExecutionException {

        System.out.println("Annotating vcf " + _inputVCF);

        File f = new File(_inputVCF);
        String subID = f.getName().replace(".vcf", "");

        try (BufferedReader brHeader = new BufferedReader(new FileReader(_inputVCF))) {
            String line;

            try (PrintWriter writer = new PrintWriter(_outputFile, "UTF-8")) {
                int currentChrom = -1;

                writer.println(_an.getVariantHeaders());

                while ((line = brHeader.readLine()) != null) {

                    if (!line.startsWith("#")) {

                        // System.out.println(line);
                        String[] fields = line.trim().split("\t");

                        // Get genotype
                        int GT = -1;
                        int DP = -1;
                        String QUAL = fields[5];

                        int geno = -1;
                        int cov = -1;

                        String[] format = fields[8].trim().split(":");
                        for (int i = 0; i < format.length; i++) {
                            if (format[i].equals("GT")) {
                                GT = i;
                            }
                            if (format[i].equals("DP")) {
                                DP = i;
                            }
                        }

                        String[] sample = fields[9].trim().split(":");
                        if (GT != -1) {
                            if (sample[GT].contains("/")) {
                                String[] genos = sample[GT].split("/");
                                geno = Integer.parseInt(genos[0]) + Integer.parseInt(genos[1]);
                                if (geno > 2) {
                                    geno = 2;
                                }
                            }
                        }

                        if (DP != -1) {
                            cov = Integer.parseInt(sample[DP]);
                        }

                        if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.]+$")) {

                            int chrom = Cytoband.getIntChrom(fields[0]);

                            if (currentChrom != chrom) {
                                System.out.println("Processing Chromosome " + chrom);
                                currentChrom = chrom;
                            }

                            String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
                                    fields[4].trim());
                            String variant = "";
                            String variantID = "";
                            if (chrom == 0) {
                                // e.g. chr1_gl000191_random
                                variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
                                        + "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
                                variantID = chrom + ":" + startStopRefAlt[0] + "_" + startStopRefAlt[1] + ":"
                                        + startStopRefAlt[3];
                            } else // chr1-23+X+Y+M+MT
                            {
                                variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
                                        + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
                                        + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
                            }
                            variantID = chrom + ":" + startStopRefAlt[0] + "_" + startStopRefAlt[1] + ":"
                                    + startStopRefAlt[3];
                            // long startTime = System.currentTimeMillis();
                            writer.println(_an.annotateVariant2(variant));
                            _an.uploadSingleVariation(geno, cov, subID, variantID);
                            // long endTime = System.currentTimeMillis();
                            // long totalTime = endTime - startTime;
                            // System.out.println("Tol "+totalTime);
                        }
                    }

                    writer.flush();

                }
            }

        }

    }

    public static String[] getVariantQuery(String testVCF) throws IOException, InterruptedException, ExecutionException {

        String keyspace = "annovar";
        String[] connectionParameters = {"localhost", "9042", keyspace};

        String table = "geneStat";

        String cytobandFile = folder + "humandb/hg19_cytoBand.txt";
        String refGene = folder + "humandb/hg19_refGene.txt";
        String refGeneMrna = folder + "humandb/hg19_refGeneMrna.fa";

        Annotation an2 = new Annotation(connectionParameters, table, cytobandFile);
        //chr,cytoband,start,end,ref,alt,Func_refGene,Gene_refGene,GeneDetail_refGene,ExonicFunc_refGene
        //esp6500siv2_all | exac_afr | exac_all | exac_amr | exac_eas | exac_fin | exac_nfe | exac_oth | exac_sas | g1000g2015aug_afr | g1000g2015aug_all | g1000g2015aug_amr | g1000g2015aug_eas | g1000g2015aug_eur | g1000g2015aug_sas
        testVCF = testVCF.replace("chr", "").replace(":", "\t.\t").replace("_", "\t").replace(";", "\t").replace(">", "\t");
        testVCF = testVCF.replace(",", "\t");
        String annotation = an2.annotateVariant2_1(testVCF);
        String[] annotations = annotation.split("\t");
        System.out.println(annotation);
        an2.closeConnection();

        return annotations;

    }

    public static ArrayList<String> getVariantFile(Annotation an, String folder, ArrayList<String> files) throws IOException, InterruptedException, ExecutionException {

        ArrayList<String> annotatedFiles = new ArrayList<String>();

        for (String file : files) {
            if (file.endsWith(".vcf")) {
                String annotatedVCFout = folder + "/" + file + ".annotated.txt";
                annotateVCF2(folder + "/" + file, an, annotatedVCFout);
                annotatedFiles.add(file + ".annotated.txt");
            }
        }

        return annotatedFiles;

    }

}
