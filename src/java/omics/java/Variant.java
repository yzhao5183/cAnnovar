/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omics.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeSet;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class Variant {

    int chr;
    int start;
    int end;
    String ref;
    String alt;

    ArrayList<String> Func_refGene;
    ArrayList<String> Gene_refGene;
    ArrayList<String> ExonicFunc_refGene;
    ArrayList<String> GeneDetail_refGene;

    String cytoband;

    String[] cassandraAnnotations;

    String other;

    String[] ucscColumnNames = {"chr", "cytoband", "start", "stop", "ref", "alt", "Func_refGene", "Gene_refGene", "GeneDetail_refGene", "ExonicFunc_refGene"};

    String[] cassandraColumnNames = {"variantid", "cadd_phred", "cadd_raw", "cg69", "clinsig", "clnacc", "clndbn", "clndsdb", "clndsdbid", "cosmic70", "dann_score", "eigen", "esp6500siv2_all", "exac_afr", "exac_all", "exac_amr", "exac_eas", "exac_fin", "exac_nfe", "exac_oth", "exac_sas", "fathmm_coding", "fathmm_mkl_coding_pred", "fathmm_mkl_coding_score", "fathmm_noncoding", "fathmm_pred", "fathmm_score", "g1000g2015aug_afr", "g1000g2015aug_all", "g1000g2015aug_amr", "g1000g2015aug_eas", "g1000g2015aug_eur", "g1000g2015aug_sas", "gerp_rs", "gerpgt2", "gwascatalog", "gwava_region_score", "gwava_tss_score", "gwava_unmatched_score", "integrated_confidence_value", "integrated_fitcons_score", "kaviar_ac", "kaviar_af", "kaviar_an", "lrt_pred", "lrt_score", "metalr_pred", "metalr_score", "metasvm_pred", "metasvm_score", "mutationassessor_pred", "mutationassessor_score", "mutationtaster_pred", "mutationtaster_score", "nci60", "phastcons20way_mammalian", "phastcons7way_vertebrate", "phylop20way_mammalian", "phylop7way_vertebrate", "polyphen2_hdiv_pred", "polyphen2_hdiv_score", "polyphen2_hvar_pred", "polyphen2_hvar_score", "provean_pred", "provean_score", "sift_pred", "sift_score", "siphy_29way_logodds", "snp138", "vest3_score", "phastConsElements46way", "tfbsConsSites", "wgRna", "targetScanS", "genomicSuperDups", "dgvMerged"};

    ArrayList<UCSCrefGene> variantAnnotations;

    public Variant() {

    }

    public Variant(String _variant) {

        String[] variant = _variant.split("\t");

        chr = Integer.parseInt(variant[0]);
        cytoband = variant[1];
        start = Integer.parseInt(variant[2]);
        end = Integer.parseInt(variant[3]);
        ref = variant[4];
        alt = variant[5];

        if (variant.length >= 7) {
            other = variant[6];
            if (variant.length > 7) {
                for (int i = 7; i <= variant.length; i++) {
                    other += "\t" + variant[i];
                }

            }
        } else {
            other = null;
        }

        Func_refGene = new ArrayList<>();
        Gene_refGene = new ArrayList<>();
        ExonicFunc_refGene = new ArrayList<>();
        GeneDetail_refGene = new ArrayList<>();

        cassandraAnnotations = null;
        
        variantAnnotations = new ArrayList<>();

    }

    public int getChr() {
        return chr;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public String getRef() {
        return ref;
    }

    public String getAlt() {
        return alt;
    }

    public String getCytoband() {
        return cytoband;
    }

    public void addAnnotation(UCSCrefGene _gene) {

        variantAnnotations.add(_gene);

    }

    public String getAnnotation() {

        String annotation = chr + "\t" + start + "\t" + end + "\t" + ref + "\t" + alt;

        int outAnnotation = -1;

        for (UCSCrefGene gene : variantAnnotations) {

        }

        return annotation;

    }

    public void setFunc_refGene(String _Func_refGene) {
//        if (Func_refGene == null) {
//            Func_refGene = _Func_refGene;
//        } else {
//            Func_refGene += "," + _Func_refGene;
//        }

//        if (_Func_refGene.equals("intergenic")) {
//            if (Func_refGene.isEmpty()) {
//                Func_refGene.add(_Func_refGene);
//            }
//        } else {
//            Func_refGene.add(_Func_refGene);
//        }
        Func_refGene.add(_Func_refGene);
        //Func_refGene = _Func_refGene;
    }

    public void setGene_refGene(String _Gene_refGene) {
//        if (Gene_refGene == null) {
//            Gene_refGene = _Gene_refGene;
//        } else {
//            Gene_refGene += "," + _Gene_refGene;
//        }

        Gene_refGene.add(_Gene_refGene);

    }

    public void setGeneDetail_refGene(String _GeneDetail_refGene) {
//        if (GeneDetail_refGene == null) {
//            GeneDetail_refGene = _GeneDetail_refGene;
//        } else {
//            GeneDetail_refGene += "," + _GeneDetail_refGene;
//        }

        String fields[] = _GeneDetail_refGene.split("\t");

        GeneDetail_refGene.add(fields[0]);

    }

    public void setExonicFunc_refGene(String _ExonicFunc_refGene) {
//        if (ExonicFunc_refGene == null) {
//            ExonicFunc_refGene = _ExonicFunc_refGene;
//        } else {
//            ExonicFunc_refGene += "," + _ExonicFunc_refGene;
//        }

        ExonicFunc_refGene.add(_ExonicFunc_refGene);

        //ExonicFunc_refGene = _ExonicFunc_refGene;
    }

    public void setCytoband(String _cytoband) {
        cytoband = _cytoband;
    }

    public void setCassandraAnnotations(String _cassandraAnnotations) {

        _cassandraAnnotations = getRSoutput(_cassandraAnnotations);
        if(_cassandraAnnotations!=null){
            cassandraAnnotations = _cassandraAnnotations.split(", ");
        }
    }

    public static String getRSoutput(String row) {
    	//System.out.println(row);
	    row = row.trim().toLowerCase().replaceFirst("^row\\[", "");
	
	    row = row.trim().replaceFirst("\\]$", "");
	
	    row = row.trim().replaceAll(" NULL", " .");
	
	
	    return row;
	}

	public String getHeaders() {
        String header = ucscColumnNames[0];

        for (int i = 1; i < ucscColumnNames.length; i++) {
            header += "\t" + ucscColumnNames[i];
        }

        for (int i = 1; i < cassandraColumnNames.length; i++) {
            header += "\t" + cassandraColumnNames[i];
        }

        return header;
    }

    public String getAnnotations() {

        String annotation = chr + "\t" + cytoband + "\t" + start + "\t" + end + "\t" + ref + "\t" + alt;

        boolean inGene = true;

        int aFunc_refGene1 = -1;
        int aFunc_refGene2 = -1;
        
        //Old Version
        if (Func_refGene.contains("exonic")) {
            aFunc_refGene1 = Func_refGene.indexOf("exonic");
            if (Func_refGene.contains("splicing")) {
                aFunc_refGene2 = Func_refGene.indexOf("splicing");
            }
        } else if (Func_refGene.contains("splicing")) {
            aFunc_refGene1 = Func_refGene.indexOf("splicing");
            if (Func_refGene.contains("ncRNA_exonic")) {
                aFunc_refGene2 = Func_refGene.indexOf("ncRNA_exonic");
            }
        } else if (Func_refGene.contains("ncRNA_exonic")) {
            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_exonic");
            if (Func_refGene.contains("splicing")) {
                aFunc_refGene2 = Func_refGene.indexOf("splicing");
            }
        } else if (Func_refGene.contains("ncRNA_splicing")) {
            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_splicing");
        } else if (Func_refGene.contains("ncRNA_intronic")) {
            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_intronic");
        } else if (Func_refGene.contains("UTR5")) {
            aFunc_refGene1 = Func_refGene.indexOf("UTR5");
            if (Func_refGene.contains("UTR3")) {
                aFunc_refGene2 = Func_refGene.indexOf("UTR3");
            }
        } else if (Func_refGene.contains("UTR3")) {
            aFunc_refGene1 = Func_refGene.indexOf("UTR3");
        } else if (Func_refGene.contains("intronic")) {
            aFunc_refGene1 = Func_refGene.indexOf("intronic");
        } else if (Func_refGene.contains("upstream")) {
            aFunc_refGene1 = Func_refGene.indexOf("upstream");
            if (Func_refGene.contains("downstream")) {
                aFunc_refGene2 = Func_refGene.indexOf("downstream");
            }
        } else if (Func_refGene.contains("downstream")) {
            aFunc_refGene1 = Func_refGene.indexOf("downstream");
        }
 
          //New version
//        if (Func_refGene.contains("exonic")) {
//            aFunc_refGene1 = Func_refGene.indexOf("exonic");
//        } else if (Func_refGene.contains("splicing")) {
//            aFunc_refGene1 = Func_refGene.indexOf("splicing");
//        } else if (Func_refGene.contains("ncRNA_exonic")) {
//            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_exonic");
//        } else if (Func_refGene.contains("ncRNA_splicing")) {
//            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_splicing");
//        } else if (Func_refGene.contains("UTR5")) {
//            aFunc_refGene1 = Func_refGene.indexOf("UTR5");
//            if (Func_refGene.contains("UTR3")) {
//                aFunc_refGene2 = Func_refGene.indexOf("UTR3");
//            }
//        } else if (Func_refGene.contains("UTR3")) {
//            aFunc_refGene1 = Func_refGene.indexOf("UTR3");
//        } else if (Func_refGene.contains("ncRNA_UTR5")) {
//            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_UTR5");
//            if (Func_refGene.contains("ncRNA_UTR3")) {
//                aFunc_refGene2 = Func_refGene.indexOf("ncRNA_UTR3");
//            }
//        } else if (Func_refGene.contains("ncRNA_UTR3")) {
//            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_UTR3");
//        } else if (Func_refGene.contains("intronic")) {
//            aFunc_refGene1 = Func_refGene.indexOf("intronic");
//        } else if (Func_refGene.contains("ncRNA_intronic")) {
//            aFunc_refGene1 = Func_refGene.indexOf("ncRNA_intronic");
//        } else if (Func_refGene.contains("upstream")) {
//            aFunc_refGene1 = Func_refGene.indexOf("upstream");
//            if (Func_refGene.contains("downstream")) {
//                aFunc_refGene2 = Func_refGene.indexOf("downstream");
//            }
//        } else if (Func_refGene.contains("downstream")) {
//            aFunc_refGene1 = Func_refGene.indexOf("downstream");
//        }
   
          //Old Version
//        if (aFunc_refGene1 != -1) {
//
//            if (aFunc_refGene1 == Func_refGene.lastIndexOf(Func_refGene.get(aFunc_refGene1))) {
//
//                //System.out.println("HERE?!\t1");
//                annotation += "\t" + Func_refGene.get(aFunc_refGene1);
//                if (aFunc_refGene2 != -1) {
//                    annotation += ";" + Func_refGene.get(aFunc_refGene2);
//                }
//
////                for (int i = 0; i < Gene_refGene.size(); i++) {
////                    System.out.println("Gene_refGene.get(i) "+ Gene_refGene.get(i));
////                    System.out.println("Gene_refGene.get(i) "+ Func_refGene.get(i));
////                }
//                //
//                if (aFunc_refGene2 != -1) {
//
////                    TreeSet<String> tGene_refGene = new TreeSet<>();
////
////                    tGene_refGene.add(Gene_refGene.get(aFunc_refGene1));
//                    annotation += "\t" + Gene_refGene.get(aFunc_refGene1);
//
//                    String aGene_refGene = Gene_refGene.get(aFunc_refGene2);
//                    for (int i = 0; i < Func_refGene.size(); i++) {
//                        if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene1)) || Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene2))) {
//                            if (!aGene_refGene.contains(Gene_refGene.get(i)) && !Gene_refGene.get(i).equals(Gene_refGene.get(aFunc_refGene1))) {
//                                aGene_refGene += "," + Gene_refGene.get(i);
//                            }
//
//                        }
//                    }
//
//                    annotation += ";" + sortCSV(aGene_refGene);
//
//                } else {
//                    TreeSet<String> tGene_refGene = new TreeSet<>();
//
//                    tGene_refGene.add(Gene_refGene.get(aFunc_refGene1));
//
//                    String aGene_refGene = Gene_refGene.get(aFunc_refGene1);
//
//                    for (int i = 0; i < Func_refGene.size(); i++) {
//                        if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene1))) {
//                            if (!tGene_refGene.contains(Gene_refGene.get(i))) {
//                                aGene_refGene += "," + Gene_refGene.get(i);
//                            }
//
//                        } else {
//
//                        }
//                    }
//                    annotation += "\t" + sortCSV(aGene_refGene);
//                }
//
//                annotation += "\t" + printHashSet(GeneDetail_refGene);
//
//                if (ExonicFunc_refGene.contains("frameshift insertion")) {
//                    annotation += "\tframeshift insertion";
//                } else if (ExonicFunc_refGene.contains("frameshift deletion")) {
//                    annotation += "\tframeshift deletion";
//                } else if (ExonicFunc_refGene.contains("frameshift block substitution")) {
//                    annotation += "\tframeshift block substitution";
//                } else if (ExonicFunc_refGene.contains("stopgain")) {
//                    annotation += "\tstopgain";
//                } else if (ExonicFunc_refGene.contains("stoploss")) {
//                    annotation += "\tstoploss";
//                } else if (ExonicFunc_refGene.contains("nonframeshift insertion")) {
//                    annotation += "\tnonframeshift insertion";
//                } else if (ExonicFunc_refGene.contains("nonframeshift deletion")) {
//                    annotation += "\tnonframeshift deletion";
//                } else if (ExonicFunc_refGene.contains("synonymous block substitution")) {
//                    annotation += "\tnonframeshift substitution";
//                } else if (ExonicFunc_refGene.contains("nonsynonymous block substitution")) {
//                    annotation += "\tnonframeshift substitution";
//                } else if (ExonicFunc_refGene.contains("nonsynonymous SNV")) {
//                    annotation += "\tnonsynonymous SNV";
//                } else if (ExonicFunc_refGene.contains("synonymous SNV")) {
//                    annotation += "\tsynonymous SNV";
//                } else if (ExonicFunc_refGene.contains("nonframeshift substitution")) {
//                    annotation += "\t.";
//                } else if (ExonicFunc_refGene.contains(".")) {
//                    annotation += "\t.";
//                }
//
//            } else {
//
////                System.out.println("Uhh?");
//                String aFunc_refGene = Func_refGene.get(aFunc_refGene1);
//                if (aFunc_refGene2 != -1) {
//                    aFunc_refGene += ";" + Func_refGene.get(aFunc_refGene2);
//                }
//                String aGene_refGene = Gene_refGene.get(aFunc_refGene1);
//
//                if (aFunc_refGene2 != -1) {
//                    aGene_refGene += ";" + Gene_refGene.get(aFunc_refGene2);
//                }
//
//                TreeSet<String> tGene_refGene = new TreeSet<>();
//
//                tGene_refGene.add(Gene_refGene.get(aFunc_refGene1));
//
//                //System.out.println("THERE?!\t2");
//                for (int i = 0; i < Func_refGene.size(); i++) {
//                    if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene1))) {
//                        if (!tGene_refGene.contains(Gene_refGene.get(i))) {
//                            aGene_refGene += "," + Gene_refGene.get(i);
//                            tGene_refGene.add(Gene_refGene.get(i));
//                        }
//
//                    } else {
//
//                    }
//                }
////
////                for (int i = 0; i < Func_refGene.size(); i++) {
////                    if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene1))) {
////                        if (!aGene_refGene.contains(Gene_refGene.get(i))) {
////                            aGene_refGene += "," + Gene_refGene.get(i);
////                        }
////
////                    }
////                }
//
//                annotation += "\t" + sortCSV(aFunc_refGene);
//
//                annotation += "\t" + sortCSV(aGene_refGene);
//                annotation += "\t" + printHashSet(GeneDetail_refGene);
//
//                if (ExonicFunc_refGene.contains("frameshift insertion")) {
//                    annotation += "\tframeshift insertion";
//                } else if (ExonicFunc_refGene.contains("frameshift deletion")) {
//                    annotation += "\tframeshift deletion";
//                } else if (ExonicFunc_refGene.contains("frameshift block substitution")) {
//                    annotation += "\tframeshift block substitution";
//                } else if (ExonicFunc_refGene.contains("stopgain")) {
//                    annotation += "\tstopgain";
//                } else if (ExonicFunc_refGene.contains("stoploss")) {
//                    annotation += "\tstoploss";
//                } else if (ExonicFunc_refGene.contains("nonframeshift insertion")) {
//                    annotation += "\tnonframeshift insertion";
//                } else if (ExonicFunc_refGene.contains("nonframeshift deletion")) {
//                    annotation += "\tnonframeshift deletion";
//                } else if (ExonicFunc_refGene.contains("synonymous block substitution")) {
//                    annotation += "\tnonframeshift substitution";
//                } else if (ExonicFunc_refGene.contains("nonsynonymous block substitution")) {
//                    annotation += "\tnonframeshift substitution";
//                } else if (ExonicFunc_refGene.contains("nonsynonymous SNV")) {
//                    annotation += "\tnonsynonymous SNV";
//                } else if (ExonicFunc_refGene.contains("synonymous SNV")) {
//                    annotation += "\tsynonymous SNV";
//                } else if (ExonicFunc_refGene.contains("nonframeshift substitution")) {
//                    annotation += "\t.";
//                } else if (ExonicFunc_refGene.contains(".")) {
//                    annotation += "\t.";
//                }
//
//                //annotation += "\t"+ExonicFunc_refGene.get(aFunc_refGene1);
//            }
//
//        } else {
//            annotation += "\tintergenic\t.\t.\t.";
//        }
        
        //New Version
        //get Func_refGene, Gene_refGene, GeneDetail_refGene
        if (aFunc_refGene1 != -1) {
        	
            TreeSet<String> tGene_refGene = new TreeSet<>();
            TreeSet<String> tGene_refGene2 = new TreeSet<>();
            TreeSet<String> tGeneDetail_refGene = new TreeSet<>();
            TreeSet<String> tGeneDetail_refGene2 = new TreeSet<>();

            tGene_refGene.add(Gene_refGene.get(aFunc_refGene1));
            tGeneDetail_refGene.add(GeneDetail_refGene.get(aFunc_refGene1));
            
            String aGene_refGene = Gene_refGene.get(aFunc_refGene1);
            String aGeneDetail_refGene = GeneDetail_refGene.get(aFunc_refGene1);

            String aGene_refGene2 = "";
            String aGeneDetail_refGene2 = "";
            
        	//get Func_refGene
            annotation += "\t" + Func_refGene.get(aFunc_refGene1);
            if (aFunc_refGene2 != -1) {
                annotation += ";" + Func_refGene.get(aFunc_refGene2);
            }
            
            //put all Func_refGene1 into string and tree
            for (int i = 0; i < Func_refGene.size(); i++) {
                if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene1))) {
                    if (!tGene_refGene.contains(Gene_refGene.get(i))) {
                        aGene_refGene += "|" + Gene_refGene.get(i);
                        tGene_refGene.add(Gene_refGene.get(i));
                    }
                    if (!tGeneDetail_refGene.contains(GeneDetail_refGene.get(i))) {
                    	aGeneDetail_refGene += "|" + GeneDetail_refGene.get(i);
                        tGeneDetail_refGene.add(GeneDetail_refGene.get(i));
                    }
                } 
            }
            
            //if have multiple Func_refGene
            if (aFunc_refGene2 != -1) {            

                tGene_refGene2.add(Gene_refGene.get(aFunc_refGene2));
                tGeneDetail_refGene2.add(GeneDetail_refGene.get(aFunc_refGene2));
                
                aGene_refGene2 = Gene_refGene.get(aFunc_refGene2);
                aGeneDetail_refGene2 = GeneDetail_refGene.get(aFunc_refGene2);
                
                //put all Func_refGene2 into string and tree
                for (int i = 0; i < Func_refGene.size(); i++) {
                    if (Func_refGene.get(i).equals(Func_refGene.get(aFunc_refGene2))) {
                        if (!tGene_refGene2.contains(Gene_refGene.get(i))) {
                            aGene_refGene2 += "|" + Gene_refGene.get(i);
                            tGene_refGene2.add(Gene_refGene.get(i));
                        }
                        if (!tGeneDetail_refGene2.contains(GeneDetail_refGene.get(i))) {
                        	aGeneDetail_refGene2 += "|" + GeneDetail_refGene.get(i);
                            tGeneDetail_refGene2.add(GeneDetail_refGene.get(i));
                        }
                    }
                }
            }
            
            //System.out.println(aGene_refGene);System.out.println(aGene_refGene2);
            //System.out.println(aGeneDetail_refGene);System.out.println(aGeneDetail_refGene2);
            //get Gene_refGene, GeneDetail_refGene
            if(!aGene_refGene2.equals("")){
                annotation += "\t" + sortCSV(aGene_refGene)+ ";" + sortCSV(aGene_refGene2);            	
                annotation += "\t" + sortCSV(aGeneDetail_refGene)+ ";" + sortCSV(aGeneDetail_refGene2);
            }
            else {
                annotation += "\t" + sortCSV(aGene_refGene);
                annotation += "\t" + sortCSV(aGeneDetail_refGene);
            }
 
            // get ExonicFunc_refGene
            if (ExonicFunc_refGene.contains("frameshift insertion")) {
                annotation += "\tframeshift insertion";
            } else if (ExonicFunc_refGene.contains("frameshift deletion")) {
                annotation += "\tframeshift deletion";
            } else if (ExonicFunc_refGene.contains("frameshift block substitution")) {
                annotation += "\tframeshift block substitution";
            } else if (ExonicFunc_refGene.contains("stopgain")) {
                annotation += "\tstopgain";
            } else if (ExonicFunc_refGene.contains("stoploss")) {
                annotation += "\tstoploss";
            } else if (ExonicFunc_refGene.contains("nonframeshift insertion")) {
                annotation += "\tnonframeshift insertion";
            } else if (ExonicFunc_refGene.contains("nonframeshift deletion")) {
                annotation += "\tnonframeshift deletion";
            } else if (ExonicFunc_refGene.contains("synonymous block substitution")) {
                annotation += "\tnonframeshift substitution";
            } else if (ExonicFunc_refGene.contains("nonsynonymous block substitution")) {
                annotation += "\tnonframeshift substitution";
            } else if (ExonicFunc_refGene.contains("nonsynonymous SNV")) {
                annotation += "\tnonsynonymous SNV";
            } else if (ExonicFunc_refGene.contains("synonymous SNV")) {
                annotation += "\tsynonymous SNV";
            } else if (ExonicFunc_refGene.contains("nonframeshift substitution")) {
                annotation += "\t.";
            } else if (ExonicFunc_refGene.contains(".")) {
                annotation += "\t.";
            }
        } else {
            annotation += "\tintergenic\t.\t.\t.";
        }
        
        // get cassandra annotation
        if (cassandraAnnotations != null) {
            for (int i = 1; i < cassandraAnnotations.length; i++) {
                if(cassandraAnnotations[i].equals("null")){
                    annotation += "\t.";
                }
                else annotation += "\t" + cassandraAnnotations[i].replace(",", ";").replace("\\s", "\'s");
            }
            if(cassandraAnnotations.length<cassandraColumnNames.length){
                for (int i = cassandraAnnotations.length; i < cassandraColumnNames.length; i++) {
                    annotation += "\t.";
                }
            }

        } else {
            System.out.println(annotation);
            for (int i = 1; i < cassandraColumnNames.length; i++) {
                annotation += "\t.";
            }
        }

        return annotation;
    }

    private String printHashSet(ArrayList<String> _arrayList) {

        String annotation = "";

        if (_arrayList.isEmpty()) {
            return ".";
        }

        for (String str : _arrayList) {
            annotation += str + ",";
        }

        return annotation.replaceFirst(",$", "");

    }

    private String sortCSV(String _str) {

        String[] fields = _str.split(",");

        Arrays.sort(fields);

        String sorted = fields[0];

        for (int i = 1; i < fields.length; i++) {
            sorted += "," + fields[i];
        }

//        for (int j = 0; j < fields.length; j++) {
//            for (int i = j + 1; i < fields.length; i++) {
//                if (fields[i].compareTo(fields[j]) < 0) {
//                    String temp = fields[j];
//                    fields[j] = fields[i];
//                    fields[i] = temp;
//
//                }
//            }
//
//            sorted+=fields[j];
//        }
        return sorted;
    }

}
