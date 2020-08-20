package org.omics.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
*
* @author Max He, PhD; Yiqing Zhao, MS
*/
public class FileProcessing {

    public static void main(String[] args) throws IOException {
//		String file = "/home/yqzhao/workspace/AnnotateVCF/data/LID57247.vcf.annotated.txt";
//		String file = "/home/yqzhao/workspace/AnnotateVCF/data/LID57247.annovar.hg19_multianno4.csv";
//		String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_esp6500siv2_all.txt";
//		examineDup(file);
//		examineDup2();
//		splitFile(file);
//		compareFile(file);
//		modifyVCFile(file);

//		String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_ALL.sites.2015_08.txt";
//        toCassandraInput(file);
//		file = "/home/yqzhao/setup/software/annovar/humandb/hg19_AFR.sites.2015_08.txt";
//        toCassandraInput(file);
//		file = "/home/yqzhao/setup/software/annovar/humandb/hg19_AMR.sites.2015_08.txt";
//        toCassandraInput(file);
//		file = "/home/yqzhao/setup/software/annovar/humandb/hg19_EAS.sites.2015_08.txt";
//        toCassandraInput(file);
//		file = "/home/yqzhao/setup/software/annovar/humandb/hg19_EUR.sites.2015_08.txt";
//        toCassandraInput(file);
//		file = "/home/yqzhao/setup/software/annovar/humandb/hg19_SAS.sites.2015_08.txt";
//        toCassandraInput(file);

        String file = "/home/yqzhao/Downloads/chr_2.vcf";
        file = "/home/yqzhao/Downloads/chr_23.vcf";
        toCassandraInput2(file);
        file = "/home/yqzhao/Downloads/chr_24.vcf";
        toCassandraInput2(file);
        file = "/home/yqzhao/Downloads/chr_25.vcf";
        toCassandraInput2(file);

        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i < 26; i++) {
            String vcf = "/home/yqzhao/Downloads/chr_" + i + ".vcf.txt";
            list.add(vcf);
        }
        PrintWriter writer = new PrintWriter("/home/yqzhao/Downloads/hg19_snp.txt", "UTF-8");
        combineFile(list, writer);
        writer.close();
    }

    public static void printArrayListCon(ArrayList<String> _list) {

        for (String str : _list) {
            System.out.println(str);
        }

    }

    public static void combineFile(ArrayList<String> _list, PrintWriter writer) throws IOException {

        for (String file : _list) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {

                String line;

                while ((line = br.readLine()) != null) {
                    writer.println(line);
                    writer.flush();
                }
            }
        }

    }

    public static void printArrayListFile(ArrayList<String> _list, String _file) throws FileNotFoundException, UnsupportedEncodingException {

        try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
            for (String str : _list) {
                writer.println(str);

                writer.flush();
            }
            writer.close();
        }

    }

    public static void toCassandraInput(String file) throws FileNotFoundException, IOException {

        PrintWriter writer = new PrintWriter(file + ".txt", "UTF-8");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] fields = line.trim().split("\t");
                String chrom = fields[0];
                String start = fields[1];
                String ref = fields[2];
                String[] _ref = ref.trim().split("");
                String end = String.valueOf(Integer.parseInt(start) + _ref.length - 1);
                String alt = fields[3];
                if (Character.isDigit(alt.charAt(0))) {
                    alt = alt.replaceAll("\\d", "");
                    if (alt.equals("")) {
                        alt = "-";
                    }
                }
                String freq = fields[4];
                String snp = fields[5];
                String out = chrom + "\t" + start + "\t" + end + "\t" + ref + "\t" + alt + "\t" + freq + "\t" + snp;
                writer.println(out);
                writer.flush();
            }
        }
        writer.close();

    }

    public static void toCassandraInput2(String file) throws FileNotFoundException, IOException {

        PrintWriter writer = new PrintWriter(file + ".txt", "UTF-8");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] fields = line.trim().split("\t");
                String chrom = fields[0];
                String start = fields[1];
                String ref = fields[3];
                String[] _ref = ref.trim().split("");
                String end = String.valueOf(Integer.parseInt(start) + _ref.length - 1);
                String alt = fields[4];
                if (Character.isDigit(alt.charAt(0))) {
                    alt = alt.replaceAll("\\d", "");
                    if (alt.equals("")) {
                        alt = "-";
                    }
                }
                String snp = fields[2];
                String out = chrom + "\t" + start + "\t" + end + "\t" + ref + "\t" + alt + "\t" + snp;
                writer.println(out);
                writer.flush();
            }
        }
        writer.close();

    }

    public static void examineDup(String file) throws FileNotFoundException, IOException {

        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        ArrayList<String> temp3 = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";

            while ((line = br.readLine()) != null) {

                String[] fields = line.trim().split("\t");
                String newID = fields[0] + ":" + fields[1] + ":" + fields[4];
                boolean add = true;
                for (int i = 0; i < temp.size(); i++) {

                    if (temp.get(i).equals(newID)) {
                        add = false;
                        System.out.println(newID);
                    }

                }
                if (add) {
                    temp.add(newID);
                }

                String newID2 = fields[0] + ":" + fields[1] + ":" + fields[2] + ":" + fields[4];
                boolean add2 = true;
                for (int i = 0; i < temp2.size(); i++) {

                    if (temp2.get(i).equals(newID2)) {
                        add2 = false;
                        System.out.println(newID2);
                    }

                }
                if (add2) {
                    temp2.add(newID2);
                }

                String newID3 = fields[0] + ":" + fields[1] + ":" + fields[2] + ":" + fields[3] + ":" + fields[4];
                boolean add3 = true;
                for (int i = 0; i < temp3.size(); i++) {

                    if (temp3.get(i).equals(newID3)) {
                        add3 = false;
                        System.out.println(newID3);
                    }

                }
                if (add3) {
                    temp3.add(newID3);
                }

            }
        }

    }

    public static void compareFile(String file) throws FileNotFoundException, IOException {

        PrintWriter writer = new PrintWriter(file + ".csv", "UTF-8");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";
            int i = 0;
            int j = 0;

            while ((line = br.readLine()) != null) {

                i++;

                String[] temp = line.trim().split(",");

                if (temp[6].equals(temp[13]) && temp[7].equals(temp[14])) {
                    line = line + ",TRUE,TRUE";
                    j++;
                } else if (!temp[6].equals(temp[13]) && temp[7].equals(temp[14])) {
                    line = line + ",FALSE,TRUE";
                } else if (temp[6].equals(temp[13]) && !temp[7].equals(temp[14])) {
                    line = line + ",TRUE,FALSE";
                } else if (!temp[6].equals(temp[13]) && !temp[7].equals(temp[14])) {
                    line = line + ",FALSE,FALSE";
                }
                writer.println(line);
                writer.flush();
                System.out.println(i);
                System.out.println(j);
            }
        }

        writer.close();

    }

    public static void modifyVCFile(String file) throws FileNotFoundException, IOException {

        PrintWriter writer = new PrintWriter(file + ".vcf", "UTF-8");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";
            int i = 0;
            int j = 0;

            while ((line = br.readLine()) != null) {

                i++;

                String[] temp = line.trim().split("\t");
                String line1 = "";
                String line2 = "";
                System.out.println(temp.length);
                if (temp[4].contains(",")) {
                    String[] alt = temp[4].trim().split(",");
                    line1 = line.replace("," + alt[1] + "\t", "\t");
                    line2 = line.replace("\t" + alt[0] + ",", "\t");
                    writer.println(line1);
                    writer.println(line2);
                    writer.flush();
                    System.out.println(line1);
                    System.out.println(line2);
                } else {
                    writer.println(line);
                    writer.flush();
                }
            }
        }

        writer.close();

    }

    public static void splitFile(String file) throws FileNotFoundException, IOException {

        ArrayList<String> temp = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";

            while ((line = br.readLine()) != null) {

                //                	line = line.replace("unknown", ".");
                temp.add(line);

            }
        }
        int splitNum = (int) Math.floor(temp.size() / 1048576) + 1;
        System.out.println(temp.size());
        System.out.println(splitNum);
        for (int i = 0; i < splitNum; i++) {
            PrintWriter writer = new PrintWriter(file + i, "UTF-8");
            for (int j = 0; j < 1048576; j++) {
                writer.println(temp.get(i * 1048576 + j));
                writer.flush();
            }
            writer.close();
        }
    }

    public static void examineDup2() throws FileNotFoundException, IOException {

        String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_ALL.sites.2015_08.txt";
        String file2 = "/home/yqzhao/setup/software/annovar/humandb/hg19_esp6500siv2_all.txt";
        ArrayList<String> temp = new ArrayList<>();
        ArrayList<String> temp2 = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line = "";

            while ((line = br.readLine()).startsWith("1")) {

                String[] fields = line.trim().split("\t");
                String newID = fields[0] + "_" + fields[1] + "_" + fields[5];
                String alt = fields[3];
                temp.add(newID + "\t" + alt);
                System.out.println(newID + "\t" + alt);
            }
        }
        System.out.println("Loaded");
        try (BufferedReader br = new BufferedReader(new FileReader(file2))) {

            String line = "";

            while ((line = br.readLine()).startsWith("1")) {

                String[] fields = line.trim().split("\t");
                String newID = fields[0] + "_" + fields[1] + "_" + fields[6];
                String alt = fields[4];
                temp2.add(newID + "\t" + alt);
            }
        }
        System.out.println("Loaded2");

        for (int i = 0; i < temp.size(); i++) {
            String[] fields = temp.get(i).trim().split("\t");
            for (int j = 0; j < temp2.size(); j++) {
                String[] fields2 = temp2.get(j).trim().split("\t");
                if (fields2[0].equals(fields[0])) {
                    System.out.println(fields[0]);
                    System.out.println(fields[1] + "," + fields2[1]);
                }
            }
        }

    }

    public static ArrayList<String> listFilesForFolder(final File folder) {
        ArrayList<String> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
                files.add(fileEntry.getName());
            }
        }
        return files;
    }

}
