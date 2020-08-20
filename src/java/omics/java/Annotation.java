/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.omics.java;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class Annotation {

	private static Cluster cluster;
	private static Session session;

	private static String[] connectionParameters;

	private static String table;

	private static Cytoband cytoband;

	private static ArrayList<UCSCrefGene> refGenes;
	private static HashMap<Integer, TreeMap<String, Integer>> refGeneIDX;
	// private static HashMap<Integer, TreeMap<Integer, String>>
	// refGeneStartIDX;

	public static HashMap<String, String> codon1;
	public static HashMap<String, String> codon3;
	public static HashMap<String, String> codon1m;
	public static HashMap<String, String> codon3m;

	TreeSet<String> toRemove = new TreeSet<>();

	public Annotation() {

		cytoband = new Cytoband();

		initCodons();

	}

	public Annotation(String[] _connectionParameters, String _table, String _file) throws IOException {

		connectionParameters = _connectionParameters;

		// table = _table;
		cassandraConnectwKeyspace(connectionParameters);

		// initCytoband(_file);
		initCodons();

	}

	private static void cassandraConnectwKeyspace(String[] _args) {

		cluster = Cluster.builder().addContactPoint(_args[0]).withPort(Integer.parseInt(_args[1])).build();
		cluster.getConfiguration().getSocketOptions().setReadTimeoutMillis(90000);
		session = cluster.connect(_args[2]);

	}

	private static void initCodons() {

		codon1 = new HashMap<>();
		codon3 = new HashMap<>();
		codon1m = new HashMap<>();
		codon3m = new HashMap<>();

		String[] condons = { "TTT", "TTC", "TCT", "TCC", "TAT", "TAC", "TGT", "TGC", "TTA", "TCA", "TAA", "TGA", "TTG",
				"TCG", "TAG", "TGG", "CTT", "CTC", "CCT", "CCC", "CAT", "CAC", "CGT", "CGC", "CTA", "CTG", "CCA", "CCG",
				"CAA", "CAG", "CGA", "CGG", "ATT", "ATC", "ACT", "ACC", "AAT", "AAC", "AGT", "AGC", "ATA", "ACA", "AAA",
				"AGA", "ATG", "ACG", "AAG", "AGG", "GTT", "GTC", "GCT", "GCC", "GAT", "GAC", "GGT", "GGC", "GTA", "GTG",
				"GCA", "GCG", "GAA", "GAG", "GGA", "GGG" };

		String[] codon3abv = { "Phe", "Phe", "Ser", "Ser", "Tyr", "Tyr", "Cys", "Cys", "Leu", "Ser", "*", "*", "Leu",
				"Ser", "*", "Trp", "Leu", "Leu", "Pro", "Pro", "His", "His", "Arg", "Arg", "Leu", "Leu", "Pro", "Pro",
				"Gln", "Gln", "Arg", "Arg", "Ile", "Ile", "Thr", "Thr", "Asn", "Asn", "Ser", "Ser", "Ile", "Thr", "Lys",
				"Arg", "Met", "Thr", "Lys", "Arg", "Val", "Val", "Ala", "Ala", "Asp", "Asp", "Gly", "Gly", "Val", "Val",
				"Ala", "Ala", "Glu", "Glu", "Gly", "Gly" };

		String[] codon1abv = { "F", "F", "S", "S", "Y", "Y", "C", "C", "L", "S", "*", "*", "L", "S", "*", "W", "L", "L",
				"P", "P", "H", "H", "R", "R", "L", "L", "P", "P", "Q", "Q", "R", "R", "I", "I", "T", "T", "N", "N", "S",
				"S", "I", "T", "K", "R", "M", "T", "K", "R", "V", "V", "A", "A", "D", "D", "G", "G", "V", "V", "A", "A",
				"E", "E", "G", "G" };

		String[] codon3abvm = { "Phe", "Phe", "Ser", "Ser", "Tyr", "Tyr", "Cys", "Cys", "Leu", "Ser", "*", "Trp", "Leu",
				"Ser", "*", "Trp", "Leu", "Leu", "Pro", "Pro", "His", "His", "Arg", "Arg", "Leu", "Leu", "Pro", "Pro",
				"Gln", "Gln", "Arg", "Arg", "Ile", "Ile", "Thr", "Thr", "Asn", "Asn", "Ser", "Ser", "Ile", "Thr", "Lys",
				"*", "Met", "Thr", "Lys", "*", "Val", "Val", "Ala", "Ala", "Asp", "Asp", "Gly", "Gly", "Val", "Val",
				"Ala", "Ala", "Glu", "Glu", "Gly", "Gly" };

		String[] codon1abvm = { "F", "F", "S", "S", "Y", "Y", "C", "C", "L", "S", "*", "W", "L", "S", "*", "W", "L",
				"L", "P", "P", "H", "H", "R", "R", "L", "L", "P", "P", "Q", "Q", "R", "R", "I", "I", "T", "T", "N", "N",
				"S", "S", "I", "T", "K", "*", "M", "T", "K", "*", "V", "V", "A", "A", "D", "D", "G", "G", "V", "V", "A",
				"A", "E", "E", "G", "G" };

		for (int i = 0; i < condons.length; i++) {

			codon1.put(condons[i], codon1abv[i]);
			codon3.put(condons[i], codon3abv[i]);
			codon1m.put(condons[i], codon1abvm[i]);
			codon3m.put(condons[i], codon3abvm[i]);
		}
	}

	public final void initCytoband(String _file) throws IOException {
		cytoband = new Cytoband(_file);
		System.out.println("Cytoband Loaded");
	}

	public void initRefGene(String _refGenFile, String _refGeneMrnaFile) throws FileNotFoundException, IOException {

		refGenes = new ArrayList<>();
		refGeneIDX = new HashMap<>();
		// refGeneStartIDX = new HashMap();

		for (int i = 0; i <= 26; i++) {

			TreeMap<String, Integer> idx = new TreeMap<>();
			TreeMap<Integer, String> startIdx = new TreeMap<>();

			// refGeneStartIDX.put(i, startIdx);
			refGeneIDX.put(i, idx);

		}

		try (BufferedReader br = new BufferedReader(new FileReader(_refGenFile))) {

			String sCurrentLine;

			int lineCount = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");

				// skip e.g. chr1_gl000191_random
				// if (fields[2].contains("_")) {
				// continue;
				// }
				// initiate a ref gene entry from UCSCrefGene file
				UCSCrefGene gene = new UCSCrefGene(sCurrentLine);

				// get gene cytoband
				gene.inputCytoBand(cytoband.getCytoBand(gene.getChrom(), gene.getTXstart()));

				// check if already exist
				if (refGeneIDX.get(gene.getChrom()).containsKey(gene.getID())) {
					continue;
				}

				// add gene entry
				refGenes.add(gene);

				// refGeneStartIDX.get(gene.getChrom()).put(gene.getTXstart(),
				// gene.getID());
				// add <chrom, <geneID, line index>>
				refGeneIDX.get(gene.getChrom()).put(gene.getID(), lineCount);

				lineCount++;

			}

			System.out.println("RefGene Loaded");
			br.close();

		}

		// parse and store ref mrna gene
		try (BufferedReader br = new BufferedReader(new FileReader(_refGeneMrnaFile))) {

			String sCurrentLine;

			int lineCount = 1;

			UCSCrefGene gene = null;

			int count = 0;

			int processing = -1;

			while ((sCurrentLine = br.readLine()) != null) {

				if (sCurrentLine.startsWith(">")) {

					lineCount++;

					String[] fields = sCurrentLine.trim().split("\\s+");

					// if (fields[7].contains("_")) {
					// continue;
					// }
					//
					// if (sCurrentLine.trim().contains("Warning")) {
					// continue;
					// }
					Pattern pattern = Pattern.compile("chr[0-9XY]+:[0-9]+");

					Matcher m = pattern.matcher(sCurrentLine);

					String[] chromStart;

					if (m.find()) {

						chromStart = m.group(0).split(":");

						int chrom = Cytoband.getIntChrom(chromStart[0]);

						int start = Cytoband.getIntChrom(chromStart[1]);

						String geneID = fields[0].replace(">", "") + "\t" + start;

						gene = refGenes.get(refGeneIDX.get(chrom).get(geneID));

						processing = lineCount;

						// System.out.println(geneID);
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				} else {

					lineCount++;

					// System.out.println("processing "+ processing);
					// System.out.println("lineCount "+ lineCount);
					if (processing + 1 == lineCount) {
						// Process mrna info
						gene.inputMrna(sCurrentLine.trim());
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				}
			}

			System.out.println("RefGeneMrna Loaded");
			br.close();
		}

	}

	public static void uploadFreqtoCassandra(String ethn, PrintWriter writer)
			throws FileNotFoundException, IOException, ExecutionException, InterruptedException {

		if (ethn.equals("esp")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_esp6500siv2_all.txt";
			writer.println("esp start");
			writer.flush();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("esp:" + variantID);
							writer.flush();
						}

						Statement insert = QueryBuilder.insertInto(connectionParameters[2], "geneStat0")
								.value("variantID", variantID).value("esp6500siv2_all", Float.parseFloat(fields[5]));

						session.executeAsync(insert);

					}
				}
			}
			writer.println("esp end");
			writer.flush();
		} else if (ethn.equals("ExAC")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_exac03.txt";
			writer.println("ExAC start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {
					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("ExAC:" + variantID);
							writer.flush();
						}
						float ExAC_ALL = 0;
						float ExAC_AFR = 0;
						float ExAC_AMR = 0;
						float ExAC_EAS = 0;
						float ExAC_FIN = 0;
						float ExAC_NFE = 0;
						float ExAC_OTH = 0;
						float ExAC_SAS = 0;
						if (!fields[5].equals(".")) {
							ExAC_ALL = Float.parseFloat(fields[5]);
						}
						if (!fields[6].equals(".")) {
							ExAC_AFR = Float.parseFloat(fields[6]);
						}
						if (!fields[7].equals(".")) {
							ExAC_AMR = Float.parseFloat(fields[7]);
						}
						if (!fields[8].equals(".")) {
							ExAC_EAS = Float.parseFloat(fields[8]);
						}
						if (!fields[9].equals(".")) {
							ExAC_FIN = Float.parseFloat(fields[9]);
						}
						if (!fields[10].equals(".")) {
							ExAC_NFE = Float.parseFloat(fields[10]);
						}
						if (!fields[11].equals(".")) {
							ExAC_OTH = Float.parseFloat(fields[11]);
						}
						if (!fields[12].equals(".")) {
							ExAC_SAS = Float.parseFloat(fields[12]);
						}

						String update = "UPDATE geneStat SET ExAC_ALL = " + ExAC_ALL + ", ExAC_AFR = " + ExAC_AFR
								+ ", ExAC_AMR = " + ExAC_AMR + ", ExAC_EAS = " + ExAC_EAS + ", ExAC_FIN = " + ExAC_FIN
								+ ", ExAC_NFE = " + ExAC_NFE + ", ExAC_OTH = " + ExAC_OTH + ", ExAC_SAS = " + ExAC_SAS
								+ " WHERE variantID = '" + variantID + "'";

						session.executeAsync(update);
						System.out.println(update);
					}
				}
			}
			writer.println("ExAC end");
			writer.flush();
		} else if (ethn.equals("gerpgt2")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_gerp++gt2.txt";
			writer.println("gerpgt2 start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":"
								+ fields[4].toUpperCase().replace("0", "-");
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("gerpgt2:" + variantID);
							writer.flush();
						}

						String update = "UPDATE geneStat SET gerpgt2 = " + Float.parseFloat(fields[5])
								+ " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("gerpgt2 end");
			writer.flush();
		} else if (ethn.equals("cg69")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("cg69 start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {
					// long startTime = System.nanoTime()/1000;
					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("cg69:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET cg69 = " + Float.parseFloat(fields[5])
								+ " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
						// long endTime = System.nanoTime()/1000;
						// long totalTime = endTime - startTime;
						// System.out.println("Tol " + totalTime);
					}
				}
			}
			writer.println("cg69 end");
			writer.flush();
		} else if (ethn.equals("clinvar")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_clinvar_20160302.txt";
			writer.println("clinvar start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("clinvar:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET CLINSIG = '" + fields[5] + "'" + ", CLNDBN = '"
								+ fields[6].replace("\'", "\\") + "'" + ", CLNACC = '" + fields[7].replace("\'", "\\")
								+ "'" + ", CLNDSDB = '" + fields[8].replace("\'", "\\") + "'" + ", CLNDSDBID = '"
								+ fields[9].replace("\'", "\\") + "'" + " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("clinvar end");
			writer.flush();
		} else if (ethn.equals("cosmic70")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("cosmic70 start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("cosmic70:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET cosmic70 = '" + fields[5] + "'" + " WHERE variantID = '"
								+ variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("cosmic70 end");
			writer.flush();
		} else if (ethn.equals("nci60")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("nci60 start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("nci60:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET nci60 = " + Float.parseFloat(fields[5])
								+ " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("nci60 end");
			writer.flush();
		} else if (ethn.equals("fathmm")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("fathmm start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("fathmm:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET FATHMM_coding = " + Float.parseFloat(fields[5])
								+ ", FATHMM_noncoding = " + Float.parseFloat(fields[6]) + " WHERE variantID = '"
								+ variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("fathmm end");
			writer.flush();
		} else if (ethn.equals("gwava")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("gwava start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("gwava:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET GWAVA_region_score = " + Float.parseFloat(fields[5])
								+ ", GWAVA_tss_score = " + Float.parseFloat(fields[6]) + ", GWAVA_unmatched_score = "
								+ Integer.parseInt(fields[7]) + " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("gwava end");
			writer.flush();
		} else if (ethn.equals("eigen")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("eigen start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("eigen:" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET eigen = " + Float.parseFloat(fields[5])
								+ " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("eigen end");
			writer.flush();
		} else if (ethn.equals("dbnsfp30a")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("dbnsfp30a start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("dbnsfp30a:" + variantID);
							writer.flush();
						}

						float SIFT_score = 0; 
						String SIFT_pred = "0";
						float Polyphen2_HDIV_score = 0; 
						String Polyphen2_HDIV_pred = "0";
						float Polyphen2_HVAR_score = 0; 
						String Polyphen2_HVAR_pred = "0";
						float LRT_score = 0; 
						String LRT_pred = "0";
						float MutationTaster_score = 0; 
						String MutationTaster_pred = "0";
						float MutationAssessor_score = 0; 
						String MutationAssessor_pred = "0";
						float FATHMM_score = 0; 
						String FATHMM_pred = "0";
						float PROVEAN_score = 0; 
						String PROVEAN_pred = "0";
						float VEST3_score = 0; 
						float CADD_raw = 0; 
						float CADD_phred = 0;
						float DANN_score = 0;
						float fathmm_MKL_coding_score = 0; 
						String fathmm_MKL_coding_pred = "0";
						float MetaSVM_score = 0; 
						String MetaSVM_pred = "0";
                                                float MetaLR_score = 0;
						String MetaLR_pred = "0";
						float integrated_fitCons_score = 0; 
						int integrated_confidence_value = 0; 
						float phyloP7way_vertebrate = 0; 
						float phyloP20way_mammalian = 0;
						float phastCons7way_vertebrate = 0; 
						float phastCons20way_mammalian = 0;
						float GERP_RS = 0; 	
						float SiPhy_29way_logOdds = 0; 

						if (!fields[5].equals(".")) {
							SIFT_score = Float.parseFloat(fields[5]);
						}
						if (!fields[6].equals(".")) {
							SIFT_pred = fields[6];
						}
						if (!fields[7].equals(".")) {
							Polyphen2_HDIV_score = Float.parseFloat(fields[7]);
						}
						if (!fields[8].equals(".")) {
							Polyphen2_HDIV_pred = fields[8];
						}
						if (!fields[9].equals(".")) {
							Polyphen2_HVAR_score = Float.parseFloat(fields[9]);
						}
						if (!fields[10].equals(".")) {
							Polyphen2_HVAR_pred = fields[10];
						}
						if (!fields[11].equals(".")) {
							LRT_score = Float.parseFloat(fields[11]);
						}
						if (!fields[12].equals(".")) {
							LRT_pred = fields[12];
						}
						if (!fields[13].equals(".")) {
							MutationTaster_score = Float.parseFloat(fields[13]);
						}
						if (!fields[14].equals(".")) {
							MutationTaster_pred = fields[14];
						}
						if (!fields[15].equals(".")) {
							MutationAssessor_score = Float.parseFloat(fields[15]);
						}
						if (!fields[16].equals(".")) {
							MutationAssessor_pred = fields[16];
						}
						if (!fields[17].equals(".")) {
							FATHMM_score = Float.parseFloat(fields[17]);
						}
						if (!fields[18].equals(".")) {
							FATHMM_pred = fields[18];
						}
						if (!fields[17].equals(".")) {
							FATHMM_score = Float.parseFloat(fields[17]);
						}
						if (!fields[18].equals(".")) {
							FATHMM_pred = fields[18];
						}
						if (!fields[19].equals(".")) {
							PROVEAN_score = Float.parseFloat(fields[19]);
						}
						if (!fields[20].equals(".")) {
							PROVEAN_pred = fields[20];
						}
						if (!fields[21].equals(".")) {
							VEST3_score = Float.parseFloat(fields[21]);
						}
						if (!fields[22].equals(".")) {
							CADD_raw = Float.parseFloat(fields[22]);
						}
						if (!fields[23].equals(".")) {
							CADD_phred = Float.parseFloat(fields[23]);
						}
						if (!fields[24].equals(".")) {
							DANN_score = Float.parseFloat(fields[24]);
						}
						if (!fields[25].equals(".")) {
							fathmm_MKL_coding_score = Float.parseFloat(fields[25]);
						}
						if (!fields[26].equals(".")) {
							fathmm_MKL_coding_pred = fields[26];
						}
						if (!fields[27].equals(".")) {
							MetaSVM_score = Float.parseFloat(fields[27]);
						}
						if (!fields[28].equals(".")) {
							MetaSVM_pred = fields[28];
						}
						if (!fields[29].equals(".")) {
							MetaLR_score = Float.parseFloat(fields[29]);
						}
						if (!fields[30].equals(".")) {
							MetaLR_pred = fields[30];
						}
						if (!fields[31].equals(".")) {
							integrated_fitCons_score = Float.parseFloat(fields[31]);
						}
						if (!fields[32].equals(".")) {
							integrated_confidence_value = Integer.parseInt(fields[32]);
						}
						if (!fields[33].equals(".")) {
							GERP_RS = Float.parseFloat(fields[33]);
						}
						if (!fields[34].equals(".")) {
							phyloP7way_vertebrate = Float.parseFloat(fields[34]);
						}
						if (!fields[35].equals(".")) {
							phyloP20way_mammalian = Float.parseFloat(fields[35]);
						}
						if (!fields[36].equals(".")) {
							phastCons7way_vertebrate = Float.parseFloat(fields[36]);
						}
						if (!fields[37].equals(".")) {
							phastCons20way_mammalian = Float.parseFloat(fields[37]);
						}
						if (!fields[38].equals(".")) {
							SiPhy_29way_logOdds = Float.parseFloat(fields[38]);
						}
						String update = "UPDATE geneStat SET SIFT_score = " + SIFT_score + ", SIFT_pred = '" + SIFT_pred
								+ "'" + ", Polyphen2_HDIV_score = " + Polyphen2_HDIV_score + ", Polyphen2_HDIV_pred = '"
								+ Polyphen2_HDIV_pred + "'" + ", Polyphen2_HVAR_score = " + Polyphen2_HVAR_score
								+ ", Polyphen2_HVAR_pred = '" + Polyphen2_HVAR_pred + "'" + ", LRT_score = " + LRT_score
								+ ", LRT_pred = '" + LRT_pred + "'" + ", MutationTaster_score = " + MutationTaster_score
								+ ", MutationTaster_pred = '" + MutationTaster_pred + "'"
								+ ", MutationAssessor_score = " + MutationAssessor_score + ", MutationAssessor_pred = '"
								+ MutationAssessor_pred + "'" + ", FATHMM_score = " + FATHMM_score + ", FATHMM_pred = '"
								+ FATHMM_pred + "'" + ", PROVEAN_score = " + PROVEAN_score + ", PROVEAN_pred = '"
								+ PROVEAN_pred + "'" + ", VEST3_score = " + VEST3_score + ", CADD_raw = " + CADD_raw
								+ ", CADD_phred = " + CADD_phred + ", DANN_score = " + DANN_score
								+ ", fathmm_MKL_coding_score = " + fathmm_MKL_coding_score
								+ ", fathmm_MKL_coding_pred = '" + fathmm_MKL_coding_pred + "'" + ", MetaSVM_score = "
								+ MetaSVM_score + ", MetaSVM_pred = '" + MetaSVM_pred + "'" + ", MetaLR_score = "
								+ MetaLR_score + ", MetaLR_pred = '" + MetaLR_pred + "'"
								+ ", integrated_fitCons_score = " + integrated_fitCons_score
								+ ", integrated_confidence_value = " + integrated_confidence_value + ", GERP_RS = "
								+ GERP_RS + ", phyloP7way_vertebrate = " + phyloP7way_vertebrate
								+ ", phyloP20way_mammalian = " + phyloP20way_mammalian + ", phastCons7way_vertebrate = "
								+ phastCons7way_vertebrate + ", phastCons20way_mammalian = " + phastCons20way_mammalian
								+ ", SiPhy_29way_logOdds = " + SiPhy_29way_logOdds + " WHERE variantID = '" + variantID
								+ "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("dbnsfp30a end");
			writer.flush();
		} else if (ethn.equals("kaviar")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_kaviar_20150923.txt";
			writer.println("kaviar start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("gwava:" + variantID);
							writer.flush();
						}
						float Kaviar_AF = 0;
						int Kaviar_AC = 0;
						int Kaviar_AN = 0;
						if (!fields[5].equals(".")) {
							Kaviar_AF = Float.parseFloat(fields[5]);
						}
						if (!fields[6].equals(".")) {
							Kaviar_AC = Integer.parseInt(fields[6]);
						}
						if (!fields[7].equals(".")) {
							Kaviar_AC = Integer.parseInt(fields[7]);
						}
						String update = "UPDATE geneStat SET Kaviar_AF = " + Kaviar_AF + ", Kaviar_AC = " + Kaviar_AC
								+ ", Kaviar_AN = " + Kaviar_AN + " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("kaviar end");
			writer.flush();
		} else if (ethn.equals("phastConsElements46way")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String phastConsElements46way = "Score=" + fields[5] + ";Name=" + fields[4];

						// String update = "UPDATE geneStat2 SET
						// phastConsElements46way = '" + phastConsElements46way
						// + "'"
						// + " WHERE chrom = " + chrom + " AND start = " + start
						// + " AND end = " + end + " AND id = '" + id + "';";
						// System.out.println(update);
						// session.executeAsync(update);

						Statement insert = QueryBuilder.insertInto(connectionParameters[2], "geneStat2").value("id", id)
								.value("chrom", chrom).value("start", start).value("end", end)
								.value("phastConsElements46way", phastConsElements46way);
						session.executeAsync(insert);
					}
				}
			}
		} else if (ethn.equals("tfbsConsSites")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String tfbsConsSites = "Score=" + fields[5] + ";Name=" + fields[4];

						String update = "UPDATE geneStat2 SET tfbsConsSites = '" + tfbsConsSites + "'"
								+ " WHERE chrom = " + chrom + " AND start = " + start + " AND end = " + end
								+ " AND id = '" + id + "';";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
		} else if (ethn.equals("wgRna")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String wgRna = "Name=" + fields[4];

						String update = "UPDATE geneStat2 SET wgRna = '" + wgRna + "'" + " WHERE chrom = " + chrom
								+ " AND start = " + start + " AND end = " + end + " AND id = '" + id + "';";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
		} else if (ethn.equals("targetScanS")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String targetScanS = "Score=" + fields[5] + ";Name=" + fields[4];

						String update = "UPDATE geneStat2 SET targetScanS = '" + targetScanS + "'" + " WHERE chrom = "
								+ chrom + " AND start = " + start + " AND end = " + end + " AND id = '" + id + "';";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
		} else if (ethn.equals("genomicSuperDups")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String genomicSuperDups = "Score=" + fields[27] + ";Name=" + fields[4];

						String update = "UPDATE geneStat2 SET genomicSuperDups = '" + genomicSuperDups + "'"
								+ " WHERE chrom = " + chrom + " AND start = " + start + " AND end = " + end
								+ " AND id = '" + id + "';";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
		} else if (ethn.equals("dgvMerged")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String id = chrom + ":" + start + "_" + end;
						String update = "UPDATE geneStat2 SET dgvMerged = '" + fields[4] + "'" + " WHERE chrom = "
								+ chrom + " AND start = " + start + " AND end = " + end + " AND id = '" + id + "';";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
		} else if (ethn.equals("snp138")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			writer.println("snp138 start");
			writer.flush();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						// System.out.println(fields[4]+";"+fields[2]+";"+fields[6]+";"+fields[7]+";"+fields[9]);

						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						String name = fields[4];
						boolean strand = fields[6].equals("+");
						String ref = fields[7];
						if (start < end) {
							start = start + 1;
						}
						ArrayList<String> alts = new ArrayList<>();
						if (strand) {
							String[] bps = fields[9].split("/");
							for (String bp : bps) {
								if (!bp.equals(ref)) {
									alts.add(bp);
								}
							}
						} else {
							String[] bps = fields[9].split("/");
							for (String bp : bps) {
								if (bp.toUpperCase().trim().matches("^[AGCT-]+$")) {
									String fbp = flipStrand(bp);
									if (!fbp.equals(ref)) {
										alts.add(fbp);
									}
								} else {
									alts.add(bp);
								}
							}
						}
						for (String alt : alts) {
							if (!strand) {
								// System.out.println(name+";"+start+";"+strand+";"+ref+";"+alt);
							}
							String variantID = chrom + ":" + start + "_" + end + ":" + alt;
							if (!alt.toUpperCase().trim().matches("^[AGCT-]+$")) {
								writer.println("snp138:" + variantID);
								writer.flush();
							}
							String update = "UPDATE geneStat SET snp138 = '" + name + "' WHERE variantID = '"
									+ variantID + "'";
							System.out.println(update);
							session.executeAsync(update);
							update = "UPDATE snp SET variantID = '" + variantID + "' WHERE name = '" + name + "'";
							System.out.println(update);
							session.executeAsync(update);
						}
					}
				}
			}
			writer.println("snp138 end");
			writer.flush();
		} else if (ethn.equals("gwasCatalog")) {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".txt";
			ArrayList<String> snps = new ArrayList<>();
			ArrayList<String> gwasCatalogs = new ArrayList<>();

			writer.println("gwas start");
			writer.flush();

			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[1]);
						int start = Integer.parseInt(fields[2]);
						int end = Integer.parseInt(fields[3]);
						if (start < end) {
							start = start + 1;
						}
						String gwasCatalog = fields[10];
						snps.add(fields[4]);
						gwasCatalogs.add(gwasCatalog.replace("\'", "\\"));
					}
				}

				for (int i = 0; i < snps.size(); i++) {
					String snp = snps.get(i);
					String gwasCatalog = "";
					// System.out.println(snp);
					for (int j = 0; j < snps.size(); j++) {
						String snp2 = snps.get(j);
						String gwasCatalog2 = gwasCatalogs.get(j);
						if (snp2.equals(snp)) {
							if (gwasCatalog.equals("")) {
								gwasCatalog = gwasCatalog2;
							} else if (!gwasCatalog.equals(gwasCatalog2)) {
								gwasCatalog = gwasCatalog + "," + gwasCatalog2;
								// System.out.println(gwasCatalog);
							}
						}
					}

					// Statement select =
					// QueryBuilder.select().all().from(connectionParameters[2],
					// "geneStat").where(eq("snp138", snp));
					String select = "SELECT * FROM snp" + " WHERE name = '" + snp + "'" + " ALLOW FILTERING;";
					System.out.println(select);

					// long startTime = System.nanoTime()/1000;

					// ResultSetFuture future = session.executeAsync(select);
					// while (!future.isDone()) {
					// System.out.println("Waiting for request to complete");
					// }
					// ResultSet rs = future.get();

					ResultSet rs = session.execute(select);

					// long endTime = System.nanoTime()/1000;
					// long totalTime = endTime - startTime;
					// System.out.println("Tol " + totalTime);

					ArrayList<String> variantIDs = new ArrayList<>();
					if (!rs.toString().startsWith("ResultSet[ exhausted: true")) {
						for (Row row : rs) {
							String[] results = Variant.getRSoutput(row.toString()).split(", ");
							String variantID = results[1];
							variantIDs.add(variantID);
							System.out.println(variantID + ":" + snp + ":" + gwasCatalog);
						}

						for (String variantID : variantIDs) {
							String update = "UPDATE geneStat SET gwasCatalog = '" + "Name=" + gwasCatalog + "'"
									+ " WHERE variantID = '" + variantID.toUpperCase() + "'";
							System.out.println(update);
							session.executeAsync(update);
						}
					} else {
						writer.println("gwas:" + snp + ":" + gwasCatalog);
						writer.flush();
					}
				}
			}
			writer.println("gwas end");
			writer.flush();
		} else {
			String file = "/home/yqzhao/setup/software/annovar/humandb/hg19_" + ethn + ".sites.2015_08.txt.txt";
			writer.println("hg19_" + ethn + " start");
			writer.flush();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {

				String line;

				while ((line = br.readLine()) != null) {

					if (!line.startsWith("#Chr")) {
						String[] fields = line.trim().split("\t");
						int chrom = Cytoband.getIntChrom(fields[0]);
						String variantID = chrom + ":" + fields[1] + "_" + fields[2] + ":" + fields[4].toUpperCase();
						if (!fields[4].toUpperCase().trim().matches("^[AGCT-]+$")) {
							writer.println("g1000g_" + ethn + ":" + variantID);
							writer.flush();
						}
						String update = "UPDATE geneStat SET g1000g2015aug_" + ethn.toLowerCase() + " = "
								+ Float.parseFloat(fields[5]) + " WHERE variantID = '" + variantID + "'";
						System.out.println(update);
						session.executeAsync(update);
					}
				}
			}
			writer.println("hg19_" + ethn + " end");
			writer.flush();
		}
	}

	public final void uploadCytoband(String _file) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(_file))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");
				String Chr = fields[0].replace("chr", "");
				int chrom = Cytoband.getIntChrom(Chr);
				int start = Integer.parseInt(fields[1]);
				int stop = Integer.parseInt(fields[2]);
				String name = fields[3];
				Statement insert = QueryBuilder.insertInto(connectionParameters[2], "cytoband").value("chrom", chrom)
						.value("start", start).value("stop", stop).value("name", name);
				session.executeAsync(insert);
			}

			br.close();
		}

		System.out.println("Cytoband Uploaded");
	}

	public final void uploadRefGene(String _file) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(_file))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");
				String id = fields[1].trim() + ":" + Cytoband.getIntChrom(fields[2].trim()) + ":"
						+ Integer.parseInt(fields[4].trim());
				int chrom = Cytoband.getIntChrom(fields[2].trim());
				boolean strand = fields[3].trim().equals("+");
				int txStart = Integer.parseInt(fields[4].trim());
				int txEnd = Integer.parseInt(fields[5].trim());
				int cdsStart = Integer.parseInt(fields[6].trim());
				int cdsEnd = Integer.parseInt(fields[7].trim());
				int exonCount = Integer.parseInt(fields[8].trim());
				String exonStarts = fields[9].trim().replace(",", ";");
				String exonEnds = fields[10].trim().replace(",", ";");
				String name = fields[12].trim();

				Statement insert = QueryBuilder.insertInto(connectionParameters[2], "refGene").value("id", id)
						.value("chrom", chrom).value("strand", strand).value("txStart", txStart).value("txEnd", txEnd)
						.value("cdsStart", cdsStart).value("cdsEnd", cdsEnd).value("exonCount", exonCount)
						.value("exonStarts", exonStarts).value("exonEnds", exonEnds).value("name", name);
				session.executeAsync(insert);

			}

			br.close();
		}

		System.out.println("RefGene Uploaded");
	}

	public final void uploadRefGene2(String _file) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(_file))) {

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");
				// String id = fields[1].trim();
				String id = fields[1].trim() + ":" + Cytoband.getIntChrom(fields[2].trim()) + ":"
						+ Integer.parseInt(fields[4].trim());
				int chrom = Cytoband.getIntChrom(fields[2].trim());
				boolean strand = fields[3].trim().equals("+");
				int txStart = Integer.parseInt(fields[4].trim());
				int txEnd = Integer.parseInt(fields[5].trim());
				int cdsStart = Integer.parseInt(fields[6].trim());
				int cdsEnd = Integer.parseInt(fields[7].trim());
				int exonCount = Integer.parseInt(fields[8].trim());
				String[] exonStarts = fields[9].trim().split(",");
				String[] exonEnds = fields[10].trim().split(",");
				String name = fields[12].trim();

				for (int i = 0; i < exonCount; i++) {
					int j = i + 1;
					String idx = id + ":" + j;
					Statement insert = QueryBuilder.insertInto(connectionParameters[2], "refGene2").value("id", idx)
							.value("chrom", chrom).value("strand", strand).value("txStart", txStart)
							.value("txEnd", txEnd).value("cdsStart", cdsStart).value("cdsEnd", cdsEnd)
							.value("exonCount", j).value("exonStart", Integer.parseInt(exonStarts[i]))
							.value("exonEnd", Integer.parseInt(exonEnds[i])).value("name", name);
					session.executeAsync(insert);
					System.out.println(idx + ":" + exonStarts[i]);
				}
			}

			br.close();
		}

		System.out.println("RefGene2 Uploaded");
	}

	public void uploadRefGeneMrna(String _refGenFile, String _refGeneMrnaFile)
			throws FileNotFoundException, IOException {

		refGenes = new ArrayList<>();
		refGeneIDX = new HashMap<>();
		// refGeneStartIDX = new HashMap();

		for (int i = 0; i <= 26; i++) {

			TreeMap<String, Integer> idx = new TreeMap<>();
			TreeMap<Integer, String> startIdx = new TreeMap<>();

			// refGeneStartIDX.put(i, startIdx);
			refGeneIDX.put(i, idx);

		}

		try (BufferedReader br = new BufferedReader(new FileReader(_refGenFile))) {

			String sCurrentLine;

			int lineCount = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");

				// skip e.g. chr1_gl000191_random
				// if (fields[2].contains("_")) {
				// continue;
				// }
				// initiate a ref gene entry from UCSCrefGene file
				UCSCrefGene gene = new UCSCrefGene(sCurrentLine);

				// get gene cytoband
				gene.inputCytoBand(cytoband.getCytoBand(gene.getChrom(), gene.getTXstart()));

				// check if already exist
				if (refGeneIDX.get(gene.getChrom()).containsKey(gene.getID())) {
					continue;
				}

				// add gene entry
				refGenes.add(gene);

				// refGeneStartIDX.get(gene.getChrom()).put(gene.getTXstart(),
				// gene.getID());
				// add <chrom, <geneID, line index>>
				refGeneIDX.get(gene.getChrom()).put(gene.getID(), lineCount);

				lineCount++;

			}

			System.out.println("RefGene Loaded");
			br.close();

		}

		// parse and store ref mrna gene
		try (BufferedReader br = new BufferedReader(new FileReader(_refGeneMrnaFile))) {

			String sCurrentLine;

			int lineCount = 1;

			UCSCrefGene gene = null;

			int count = 0;

			int processing = -1;

			while ((sCurrentLine = br.readLine()) != null) {

				if (sCurrentLine.startsWith(">")) {

					lineCount++;

					String[] fields = sCurrentLine.trim().split("\\s+");

					// if (fields[7].contains("_")) {
					// continue;
					// }
					//
					// if (sCurrentLine.trim().contains("Warning")) {
					// continue;
					// }
					Pattern pattern = Pattern.compile("chr[0-9XY]+:[0-9]+");

					Matcher m = pattern.matcher(sCurrentLine);

					String[] chromStart;

					if (m.find()) {

						chromStart = m.group(0).split(":");

						int chrom = Cytoband.getIntChrom(chromStart[0]);

						int start = Cytoband.getIntChrom(chromStart[1]);

						String geneID = fields[0].replace(">", "") + "\t" + chrom + "\t" + start;

						gene = refGenes.get(refGeneIDX.get(chrom).get(geneID));

						processing = lineCount;

						// System.out.println(geneID);
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				} else {

					lineCount++;

					// System.out.println("processing "+ processing);
					// System.out.println("lineCount "+ lineCount);
					if (processing + 1 == lineCount) {
						// Process mrna info
						gene.inputMrna(sCurrentLine.trim());
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				}

				String id = gene.getID().trim().replace("\t", ":");
				String[] fields = gene.getID().split("\t");
				String id0 = fields[0];
				int chrom = gene.getChrom();
				int txStart = gene.getTXstart();
				int txEnd = gene.getTXend();
				int cdsStart = gene.getCDSstart();
				int cdsEnd = gene.getCDSend();
				int exonCount = gene.getExonCount();
				int[] exonStarts = gene.getExonStarts();
				int[] exonEnds = gene.getExonEnds();
				String Mrna = gene.getMrna();
				String flippedMrna = gene.getflippedMrna();
				boolean strand = gene.getStrand();
				if (!strand) {
					Mrna = flippedMrna;
				}
				String Protein1 = gene.getProtein1();
				int mrnaStartOffset = gene.getmrnaStartOffset();
				int mrnaEndOffset = gene.getmrnaEndOffset();

				// String update = "UPDATE refGene SET Mrna = '" + Mrna + "'" +
				// ", Protein1 = '" + Protein1 + "'"
				// + ", mrnaStartOffset = " + mrnaStartOffset + ", mrnaEndOffset
				// = " + mrnaEndOffset
				// + " WHERE id = '" + id + "' AND chrom = "+ chrom + " AND
				// txStart = "+ txStart
				// + " AND txEnd = "+ txEnd + ";";
				// session.executeAsync(update);
				if (id.startsWith("NR") && Mrna != null) {
					for (int i = 0; i < exonCount; i++) {
						int j = i + 1;
						// String idx = id0 + ":" + j;
						String idx = id + ":" + j;
						System.out.println(idx);
						// System.out.println(gene.getMrna());
						// System.out.println(gene.getProtein1());
						String update = "UPDATE refGene2 SET Mrna = '" + Mrna + "'" + ", Protein1 = '" + Protein1 + "'"
								+ ", mrnaStartOffset = " + mrnaStartOffset + ", mrnaEndOffset = " + mrnaEndOffset
								+ ", nonCoding = " + true + " WHERE id = '" + idx + "' AND chrom = " + chrom
								+ " AND txStart = " + txStart + " AND txEnd = " + txEnd + " AND cdsStart = " + cdsStart
								+ " AND cdsEnd = " + cdsEnd + " AND exonStart = " + exonStarts[i] + " AND exonEnd = "
								+ exonEnds[i] + ";";
						session.executeAsync(update);
					}
				} else if (Mrna != null) {
					for (int i = 0; i < exonCount; i++) {
						int j = i + 1;
						// String idx = id0 + ":" + j;
						String idx = id + ":" + j;
						System.out.println(idx);
						// System.out.println(gene.getMrna());
						// System.out.println(gene.getProtein1());
						String update = "UPDATE refGene2 SET Mrna = '" + Mrna + "'" + ", Protein1 = '" + Protein1 + "'"
								+ ", mrnaStartOffset = " + mrnaStartOffset + ", mrnaEndOffset = " + mrnaEndOffset
								+ ", nonCoding = " + false + " WHERE id = '" + idx + "' AND chrom = " + chrom
								+ " AND txStart = " + txStart + " AND txEnd = " + txEnd + " AND cdsStart = " + cdsStart
								+ " AND cdsEnd = " + cdsEnd + " AND exonStart = " + exonStarts[i] + " AND exonEnd = "
								+ exonEnds[i] + ";";
						session.executeAsync(update);
					}
				}
			}
			br.close();
		}
		System.out.println("RefGeneMrna Uploaded");
	}

	public void filterCassandraRefGene(String _refGenFile, String _refGeneMrnaFile)
			throws FileNotFoundException, IOException {

		refGenes = new ArrayList<>();
		refGeneIDX = new HashMap<>();
		// refGeneStartIDX = new HashMap();

		for (int i = 0; i <= 26; i++) {

			TreeMap<String, Integer> idx = new TreeMap<>();
			TreeMap<Integer, String> startIdx = new TreeMap<>();

			// refGeneStartIDX.put(i, startIdx);
			refGeneIDX.put(i, idx);

		}

		try (BufferedReader br = new BufferedReader(new FileReader(_refGenFile))) {

			String sCurrentLine;

			int lineCount = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				String[] fields = sCurrentLine.trim().split("\t");

				// skip e.g. chr1_gl000191_random
				// if (fields[2].contains("_")) {
				// continue;
				// }
				// initiate a ref gene entry from UCSCrefGene file
				UCSCrefGene gene = new UCSCrefGene(sCurrentLine);

				// get gene cytoband
				gene.inputCytoBand(cytoband.getCytoBand(gene.getChrom(), gene.getTXstart()));

				// check if already exist
				if (refGeneIDX.get(gene.getChrom()).containsKey(gene.getID())) {
					continue;
				}

				// add gene entry
				refGenes.add(gene);

				// refGeneStartIDX.get(gene.getChrom()).put(gene.getTXstart(),
				// gene.getID());
				// add <chrom, <geneID, line index>>
				refGeneIDX.get(gene.getChrom()).put(gene.getID(), lineCount);

				lineCount++;

			}

			System.out.println("RefGene Loaded");
			br.close();

		}

		HashMap<String, HashSet<String>> byName = new HashMap<>();

		int nullProtienCount = 0;

		int rnaCount = 0;

		// byName = <gene name, <gene id>>
		for (UCSCrefGene gene : refGenes) {
			if (byName.containsKey(gene.getName())) {
				byName.get(gene.getName()).add(gene.getID());
			} else {
				HashSet<String> id = new HashSet<>();
				id.add(gene.getID());
				byName.put(gene.getName(), id);
			}

		}

		// when a gene name has multiple id, and have at least
		// one id.startsWith("NM"), add id.startsWith("NR") to toRemove
		// TreeSet <String> toRemove = new TreeSet<>();
		for (String name : byName.keySet()) {
			TreeSet<String> toAdd = new TreeSet<>();
			boolean hasNM = false;
			for (String id : byName.get(name)) {
				if (id.startsWith("NR")) {
					toAdd.add(id);
				} else {
					hasNM = true;
				}
			}
			if (hasNM == true) {
				toRemove.addAll(toAdd);
			}

		}

		// for(int i =0; i<refGenes.size();i++){
		// if(toRemove.contains(refGenes.get(i).getID())){
		// refGenes.remove(i);
		// }
		// }
		// parse and store ref mrna gene
		try (BufferedReader br = new BufferedReader(new FileReader(_refGeneMrnaFile))) {

			String sCurrentLine;

			int lineCount = 1;

			UCSCrefGene gene = null;

			int count = 0;

			int processing = -1;

			while ((sCurrentLine = br.readLine()) != null) {

				if (sCurrentLine.startsWith(">")) {

					lineCount++;

					String[] fields = sCurrentLine.trim().split("\\s+");

					// if (fields[7].contains("_")) {
					// continue;
					// }
					//
					// if (sCurrentLine.trim().contains("Warning")) {
					// continue;
					// }
					Pattern pattern = Pattern.compile("chr[0-9XY]+:[0-9]+");

					Matcher m = pattern.matcher(sCurrentLine);

					String[] chromStart;

					if (m.find()) {

						chromStart = m.group(0).split(":");

						int chrom = Cytoband.getIntChrom(chromStart[0]);

						int start = Cytoband.getIntChrom(chromStart[1]);

						String geneID = fields[0].replace(">", "") + "\t" + chrom + "\t" + start;

						gene = refGenes.get(refGeneIDX.get(chrom).get(geneID));

						processing = lineCount;

						// System.out.println(geneID);
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				} else {

					lineCount++;

					// System.out.println("processing "+ processing);
					// System.out.println("lineCount "+ lineCount);
					if (processing + 1 == lineCount) {
						// Process mrna info
						gene.inputMrna(sCurrentLine.trim());
					} else {
						// System.out.println(sCurrentLine.trim());
						// System.exit(0);
					}

				}

				String id = gene.getID().trim();
				int chrom = gene.getChrom();
				int txStart = gene.getTXstart();
				int txEnd = gene.getTXend();
				int cdsStart = gene.getCDSstart();
				int cdsEnd = gene.getCDSend();
				int exonCount = gene.getExonCount();
				int[] exonStarts = gene.getExonStarts();
				int[] exonEnds = gene.getExonEnds();

				// for (String idx : toRemove){
				// if(id.equals(idx)){
				// System.out.println(idx);
				// String update = "UPDATE refGene SET filter = " + true
				// + " WHERE id = '" + id.replace("\t", ":")
				// + "' AND chrom = "+ chrom
				// + " AND txStart = "+ txStart
				// + " AND txEnd = "+ txEnd + ";";
				// System.out.println(update);
				// session.executeAsync(update);
				// }
				// }
				for (String idr : toRemove) {
					if (id.equals(idr)) {
						String[] fields = id.split("\t");
						String id0 = fields[0];
						id = id.replace("\t", ":");
						for (int i = 0; i < exonCount; i++) {
							int j = i + 1;
							// String idx = id0 + ":" + j;
							String idx = id + ":" + j;
							System.out.println(idx);
							String update = "UPDATE refGene2 SET filter = " + true + " WHERE id = '" + idx
									+ "' AND chrom = " + chrom + " AND txStart = " + txStart + " AND txEnd = " + txEnd
									+ " AND cdsStart = " + cdsStart + " AND cdsEnd = " + cdsEnd + " AND exonStart = "
									+ exonStarts[i] + " AND exonEnd = " + exonEnds[i] + ";";
							System.out.println(update);
							session.executeAsync(update);
						}
					}
				}
			}
			br.close();
		}
		System.out.println("Filter Uploaded");

	}

	public final void uploadAnnotatedVar(String _file) throws IOException {

		try (BufferedReader br = new BufferedReader(new FileReader(_file))) {

			String sCurrentLine;

			// chr cytoband start stop ref alt Func_refGene
			// Gene_refGene GeneDetail_refGene ExonicFunc_refGene
			// cadd_phred cadd_raw cg69 clinsig clnacc clndbn clndsdb clndsdbid cosmic70 
			// dann_score eigen esp6500siv2_all exac_afr exac_all exac_amr exac_eas 
			// exac_fin exac_nfe exac_oth exac_sas fathmm_coding fathmm_mkl_coding_pred 
			// fathmm_mkl_coding_score fathmm_noncoding fathmm_pred fathmm_score g1000g2015aug_afr 
			// g1000g2015aug_all g1000g2015aug_amr g1000g2015aug_eas g1000g2015aug_eur 
			// g1000g2015aug_sas gerp_rs gerpgt2 gwascatalog gwava_region_score gwava_tss_score 
			// gwava_unmatched_score integrated_confidence_value integrated_fitcons_score 
			// kaviar_ac kaviar_af kaviar_an lrt_pred lrt_score metalr_pred metalr_score 
			// metasvm_pred metasvm_score mutationassessor_pred mutationassessor_score 
			// mutationtaster_pred mutationtaster_score nci60 phastcons20way_mammalian 
			// phastcons7way_vertebrate phylop20way_mammalian phylop7way_vertebrate 
			// polyphen2_hdiv_pred polyphen2_hdiv_score polyphen2_hvar_pred polyphen2_hvar_score 
			// provean_pred provean_score sift_pred sift_score siphy_29way_logodds snp138 vest3_score 
			// phastConsElements46way tfbsConsSites wgRna targetScanS genomicSuperDups dgvMerged


			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.startsWith("chr")) {
					String[] fields = sCurrentLine.trim().split("\t");
					String variantID = fields[0] + ":" + fields[2] + "_" + fields[3] + ":" + fields[5];
					String select = "SELECT * FROM variants" + " WHERE variantID = '" + variantID
							+ "' ALLOW FILTERING;";

					String rs = session.execute(select).toString();

					if (rs.startsWith("ResultSet[ exhausted: true")) {
						int chrom = Integer.parseInt(fields[0]);
						String cytoband = fields[1];
						int start = Integer.parseInt(fields[2]);
						int stop = Integer.parseInt(fields[3]);
						String ref = fields[4];
						String alt = fields[5];
						String Func_refGene = fields[6];
						String Gene_refGene = fields[7];
						String GeneDetail_refGene = fields[8];
						String ExonicFunc_refGene = fields[9];

						String cadd_phred = fields[10];
						String cadd_raw = fields[11];
						String cg69 = fields[12];
						String clinsig = fields[13];
						String clnacc = fields[14];
						String clndbn = fields[15];
						String clndsdb = fields[16];
						String clndsdbid = fields[17];
						String cosmic70 = fields[18];
						String dann_score = fields[19];
						String eigen = fields[20];
						String esp6500siv2_all = fields[21];
						String exac_afr = fields[22];
						String exac_all = fields[23];
						String exac_amr = fields[24];
						String exac_eas = fields[25];
						String exac_fin = fields[26];
						String exac_nfe = fields[27];
						String exac_oth = fields[28];
						String exac_sas = fields[29];
						String fathmm_coding = fields[30];
						String fathmm_mkl_coding_pred = fields[31];
						String fathmm_mkl_coding_score = fields[32];
						String fathmm_noncoding = fields[33];
						String fathmm_pred = fields[34];
						String fathmm_score = fields[35];
						String g1000g2015aug_afr = fields[36];
						String g1000g2015aug_all = fields[37];
						String g1000g2015aug_amr = fields[38];
						String g1000g2015aug_eas = fields[39];
						String g1000g2015aug_eur = fields[40];
						String g1000g2015aug_sas = fields[41];
						String gerp_rs = fields[42];
						String gerpgt2 = fields[43];
						String gwascatalog = fields[44];
						String gwava_region_score = fields[45];
						String gwava_tss_score = fields[46];
						String gwava_unmatched_score = fields[47];
						String integrated_confidence_value = fields[48];
						String integrated_fitcons_score = fields[49];
						String kaviar_ac = fields[50];
						String kaviar_af = fields[51];
						String kaviar_an = fields[52];
						String lrt_pred = fields[53];
						String lrt_score = fields[54];
						String metalr_pred = fields[55];
						String metalr_score = fields[56];
						String metasvm_pred = fields[57];
						String metasvm_score = fields[58];
						String mutationassessor_pred = fields[59];
						String mutationassessor_score = fields[60];
						String mutationtaster_pred = fields[61];
						String mutationtaster_score = fields[62];
						String nci60 = fields[63];
						String phastcons20way_mammalian = fields[64];
						String phastcons7way_vertebrate = fields[65];
						String phylop20way_mammalian = fields[66];
						String phylop7way_vertebrate = fields[67];
						String polyphen2_hdiv_pred = fields[68];
						String polyphen2_hdiv_score = fields[69];
						String polyphen2_hvar_pred = fields[70];
						String polyphen2_hvar_score = fields[71];
						String provean_pred = fields[72];
						String provean_score = fields[73];
						String sift_pred = fields[74];
						String sift_score = fields[75];
						String siphy_29way_logodds = fields[76];
						String snp138 = fields[77];
						String vest3_score = fields[78];
						String phastConsElements46way = fields[79];
						String tfbsConsSites = fields[80];
						String wgRna = fields[81];
						String targetScanS = fields[82];
						String genomicSuperDups = fields[83];
						String dgvMerged = fields[84];

						Statement insert = QueryBuilder.insertInto(connectionParameters[2], "variants").value("variantID", variantID)
								.value("chrom", chrom).value("cytoband", cytoband).value("start", start).value("stop", stop)
								.value("ref", ref).value("alt", alt).value("Func_refGene", Func_refGene)
								.value("Gene_refGene", Gene_refGene).value("GeneDetail_refGene", GeneDetail_refGene)
								.value("ExonicFunc_refGene", ExonicFunc_refGene).value("cadd_phred", cadd_phred)
								.value("cadd_raw", cadd_raw).value("cg69", cg69).value("clinsig", clinsig).value("clnacc", clnacc)
								.value("clndbn", clndbn).value("clndsdb", clndsdb).value("clndsdbid", clndsdbid).value("cosmic70", cosmic70)
								.value("dann_score", dann_score).value("eigen", eigen).value("esp6500siv2_all", esp6500siv2_all)
								.value("exac_afr", exac_afr).value("exac_all", exac_all).value("exac_amr", exac_amr)
								.value("exac_eas", exac_eas).value("exac_fin", exac_fin).value("exac_nfe", exac_nfe)
								.value("exac_oth", exac_oth).value("exac_sas", exac_sas).value("fathmm_coding", fathmm_coding)
								.value("fathmm_mkl_coding_pred", fathmm_mkl_coding_pred)
								.value("fathmm_mkl_coding_score", fathmm_mkl_coding_score).value("fathmm_noncoding", fathmm_noncoding)
								.value("fathmm_pred", fathmm_pred).value("fathmm_score", fathmm_score)
								.value("g1000g2015aug_afr", g1000g2015aug_afr).value("g1000g2015aug_all", g1000g2015aug_all)
								.value("g1000g2015aug_amr", g1000g2015aug_amr).value("g1000g2015aug_eas", g1000g2015aug_eas)
								.value("g1000g2015aug_eur", g1000g2015aug_eur).value("g1000g2015aug_sas", g1000g2015aug_sas)
								.value("gerp_rs", gerp_rs).value("gerpgt2", gerpgt2)
								.value("gwascatalog", gwascatalog).value("gwava_region_score", gwava_region_score)
								.value("gwava_tss_score", gwava_tss_score).value("gwava_unmatched_score", gwava_unmatched_score)
								.value("integrated_confidence_value", integrated_confidence_value).value("integrated_fitcons_score", integrated_fitcons_score)
								.value("kaviar_ac", kaviar_ac).value("kaviar_af", kaviar_af).value("kaviar_an", kaviar_an)
								.value("lrt_pred", lrt_pred).value("lrt_score", lrt_score).value("metalr_pred", metalr_pred)
								.value("metalr_score", metalr_score).value("metasvm_pred", metasvm_pred).value("metasvm_score", metasvm_score)
								.value("mutationassessor_pred", mutationassessor_pred).value("mutationassessor_score", mutationassessor_score).value("mutationtaster_pred", mutationtaster_pred)
								.value("mutationtaster_score", mutationtaster_score).value("nci60", nci60).value("phastcons20way_mammalian", phastcons20way_mammalian)
								.value("phastcons7way_vertebrate", phastcons7way_vertebrate).value("phylop20way_mammalian", phylop20way_mammalian)
								.value("phylop7way_vertebrate", phylop7way_vertebrate).value("polyphen2_hdiv_pred", polyphen2_hdiv_pred)
								.value("polyphen2_hdiv_score", polyphen2_hdiv_score).value("polyphen2_hvar_pred", polyphen2_hvar_pred)
								.value("polyphen2_hvar_score", polyphen2_hvar_score).value("provean_pred", provean_pred)
								.value("provean_score", provean_score).value("sift_pred", sift_pred)
								.value("sift_score", sift_score).value("siphy_29way_logodds", siphy_29way_logodds)
								.value("snp138", snp138).value("vest3_score", vest3_score)
								.value("phastConsElements46way", phastConsElements46way).value("tfbsConsSites", tfbsConsSites)
								.value("wgRna", wgRna).value("targetScanS", targetScanS)
								.value("genomicSuperDups", genomicSuperDups).value("dgvMerged", dgvMerged);
						
						session.executeAsync(insert);
						System.out.println("Variant "+variantID+" Uploaded");
					}
				}
			}
			br.close();
		}

		System.out.println("Variants Uploaded");
	}

	public final void uploadVariation(String _file) throws IOException {

		File f = new File(_file);
		String subID = f.getName().replace(".vcf", "");
		String varID = "";
		String id = "";

		try (BufferedReader br = new BufferedReader(new FileReader(_file))) {

			int currentChrom = -1;
			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				if (!sCurrentLine.startsWith("#")) {
					String[] fields = sCurrentLine.trim().split("\t");

					if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.]+$")) {

						int chrom = Cytoband.getIntChrom(fields[0]);

						if (currentChrom != chrom) {
							System.out.println("Processing Chromosome " + chrom);
							currentChrom = chrom;
						}

						String[] startStopRefAlt = toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
								fields[4].trim());
						varID = chrom + ":" + startStopRefAlt[0] + "_" + startStopRefAlt[1] + ":" + startStopRefAlt[3];
						id = subID + ":" + varID;
					}

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
					if (sample[GT].contains("/")) {
						String[] genos = sample[GT].split("/");
						geno = Integer.parseInt(genos[0]) + Integer.parseInt(genos[1]);
						if (geno > 2) {
							geno = 2;
						}
					}
					if (sample[DP].contains(":")) {

					} else {
						cov = Integer.parseInt(sample[DP]);
					}

					// Statement insert =
					// QueryBuilder.insertInto(connectionParameters[2],
					// "variation")
					// .value("id", id).value("subID", subID).value("varID",
					// varID)
					// .value("geno", geno).value("cov", cov);
					// session.executeAsyncAsync(insert);
					String update = "UPDATE variation SET geno = " + geno + " WHERE id = '" + id + "'"
							+ " AND subID = '" + subID + "'" + " AND varID = '" + varID + "'" + " AND cov = " + cov
							+ ";";
					System.out.println(update);
					session.executeAsync(update);

				}
			}
			br.close();
		}

		System.out.println("Variations Uploaded");
	}

	public String getVariantHeaders() {

		Variant variant = new Variant();

		return variant.getHeaders();
	}

	public String annotateVariant(String _variant) {

		Variant variant = new Variant(_variant);

		ArrayList<UCSCrefGene> genes = getGenes(variant.chr, variant.start, variant.end);

		ArrayList<String> cassandraAnnotations = annotateCassandraFromAVINPUT(variant.chr, variant.cytoband,
				variant.start, variant.end, variant.ref, variant.alt);

		if (!cassandraAnnotations.isEmpty()) {
			for (String annotation : cassandraAnnotations) {
				variant.setCassandraAnnotations(annotation);
			}
		}

		if (genes.isEmpty()) {
			variant.setFunc_refGene("intergenic");
		} else {
			for (UCSCrefGene gene : genes) {

				String aFunc_refGene = "intergenic";
				String aExonicFunc_refGene = ".";

				if (gene.getID().startsWith("NR")) {

					// System.out.println("gene.getName() "+ gene.getName());
					if (gene.betweenTX(variant.start, variant.end)) {

						if (gene.isSplice(variant.start, variant.end)) {
							aFunc_refGene = "ncRNA_splicing";

						} else if (gene.inExon(variant.start, variant.end) >= 0) {
							aFunc_refGene = "ncRNA_exonic";

						} else if (gene.inExon(variant.start, variant.end) < 0) {
							aFunc_refGene = "ncRNA_intronic";
						}
					} else if (gene.isDownStream(variant.start, variant.end)) {

						if (gene.getStrand()) {
							aFunc_refGene = "downstream";
						} else {
							aFunc_refGene = "upstream";

						}
					} else if (gene.isUpStream(variant.start, variant.end)) {

						if (gene.getStrand()) {
							aFunc_refGene = "upstream";
						} else {
							aFunc_refGene = "downstream";
						}

					} else {
						aFunc_refGene = "intergenic";
					}

				} else if (gene.betweenTX(variant.start, variant.end)) {

					if (gene.isSplice(variant.start, variant.end)) {
						aFunc_refGene = "splicing";
					} else if (gene.inExon(variant.start, variant.end) == -1) {

						aFunc_refGene = "intronic";

						// Annovar result tag exonic from betweenCDS
					} else if (gene.betweenCDS(variant.start, variant.end)) {
						aFunc_refGene = "exonic";
						aExonicFunc_refGene = gene.getExonicFunc_refGene(variant.start, variant.end, variant.ref,
								variant.alt);
					} else if (gene.in3utr(variant.start, variant.end)) {
						if (gene.getStrand()) {
							aFunc_refGene = "UTR3";
						} else {
							aFunc_refGene = "UTR5";
						}

					} else if (gene.in5utr(variant.start, variant.end)) {
						if (gene.getStrand()) {
							aFunc_refGene = "UTR5";
						} else {
							aFunc_refGene = "UTR3";
						}
					}

				} else if (gene.isDownStream(variant.start, variant.end)) {
					if (gene.getStrand()) {
						aFunc_refGene = "downstream";
					} else {
						aFunc_refGene = "upstream";
					}
				} else if (gene.isUpStream(variant.start, variant.end)) {

					if (gene.getStrand()) {
						aFunc_refGene = "upstream";
					} else {
						aFunc_refGene = "downstream";
					}

				} else {
					aFunc_refGene = "intergenic";
				}

				variant.setGene_refGene(gene.getName());
				variant.setGeneDetail_refGene(gene.getID());
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}

		}

		return variant.getAnnotations();

	}

	public ArrayList<UCSCrefGene> getGenes(int _chrom, int _start, int _end) {

		TreeMap<String, Integer> chrom = refGeneIDX.get(_chrom);

		ArrayList<UCSCrefGene> genes = new ArrayList<>();

		for (String key : chrom.keySet()) {
			// System.out.println("HERE?!");
			int idx = chrom.get(key);

			if (toRemove.contains(refGenes.get(idx).getID())) {
				continue;
			}

			if (refGenes.get(idx).betweenTX(_start, _end)) {
				genes.add(refGenes.get(idx));
			} else if (refGenes.get(idx).isUpStream(_start, _end)) {

				genes.add(refGenes.get(idx));
			} else if (refGenes.get(idx).isDownStream(_start, _end)) {

				genes.add(refGenes.get(idx));
			}
		}

		// for (Integer key : chrom.keySet()) {
		//
		// if (_start >= (key - 1)) {
		//
		// String id = chrom.get(key);
		//
		// int idx = refGeneIDX.get(_chrom).get(id);
		//
		// if (_start <= refGenes.get(idx).getTXend() + 1) {
		// genes.add(refGenes.get(idx));
		// }
		//
		// }
		//
		// }
		return genes;

	}

	public ArrayList<String> annotateCassandraFromVCF(int _chr, int _start, String _ref, String _alt) {

		String[] id = toAvinput(_start, _ref, _alt);

		String band = cytoband.getCytoBand(_chr, _start);

		ArrayList<String> annotation = new ArrayList<>();

		ResultSet results;

		Statement select = QueryBuilder.select().all().from(connectionParameters[2], "geneStat").where(eq("chr", _chr))
				.and(eq("cytoband", band)).and(eq("start", id[0])).and(eq("stop", id[1])).and(eq("ref", id[2]))
				.and(eq("alt", id[3]));
		results = session.execute(select);

		for (Row row : results) {

			annotation.add(row.toString());
		}

		return annotation;
	}

	public ArrayList<String> annotateCassandraFromAVINPUT(int _chr, String _cytoband, int _start, int _stop,
			String _ref, String _alt) {

		ArrayList<String> annotation = new ArrayList<>();
		ResultSet results;

		// Statement select =
		// QueryBuilder.select().all().from(connectionParameters[2], "geneStat")
		// .where(eq("chr", _chr))
		// .and(eq("cytoband", _cytoband))
		// .and(eq("start", _start))
		// .and(eq("stop", _stop))
		// .and(eq("ref", _ref))
		// .and(eq("alt", _alt));
		String variantID = _chr + ":" + _start + "_" + _stop + ":" + _alt;
		Statement select = QueryBuilder.select().all().from(connectionParameters[2], "geneStat")
				.where(eq("variantID", variantID));

		results = session.execute(select);

		for (Row row : results) {

			annotation.add(row.toString());
		}

		return annotation;
	}

	public void closeConnection() {
		session.close();
		cluster.close();
	}

	public static String[] toAvinput(int _start, String _ref, String _alt) {

		// Ensures correct formatting of reference and alternative allele
		if (!(_ref.matches("^[AGCT\\.]+$") && _alt.matches("^[AGCT\\.]+$"))) {

			System.out.println(_ref + "\t" + _alt);
			return null;
		}

		char[] ref = _ref.toCharArray();
		char[] alt = _alt.toCharArray();

		int stop; // = -1;

		if (ref.length == 1 && ref.length == alt.length && !_ref.contains(".") && !_alt.contains(".")) { // if
			// SNV
			stop = _start;
		} else if (ref.length == 1 && ref.length <= alt.length && _ref.contains(".") && !_alt.contains(".")) { // if
			// insertion
			stop = _start + alt.length;
		} else if (alt.length == 1 && ref.length >= alt.length && !_ref.contains(".") && _alt.contains(".")) { // if
			// deletion
			stop = _start + ref.length;
			_start = _start - 1;
		} else if (ref.length >= alt.length && !_ref.contains(".") && !_alt.contains(".")) { // if
			// another
			// kind
			// of
			// deletion
			// or
			// snv
			// or
			// block
			// substitution

			int numMatchChar = 0;

			for (int i = 0; i < alt.length; i++) { // determines the number of
				// starting matching bases
				// in reference and
				// alternative alleles
				if (ref[i] == alt[i]) {
					numMatchChar++;
				} else {
					break;
				}
			}

			stop = _start + ref.length - 1;// Variant Stop

			if (numMatchChar == alt.length) { // is a deletion?

				_start += numMatchChar;

				if (numMatchChar > 0) {
					String newRef = "";
					String newAlt = "";

					for (int i = numMatchChar; i < alt.length; i++) {
						newAlt += alt[i];
					}
					for (int i = numMatchChar; i < ref.length; i++) {
						newRef += ref[i];
					}

					if (newAlt.equals("")) {
						newAlt = ".";
					}

					_ref = newRef;
					_alt = newAlt;

				}
			}

		} else if (alt.length >= ref.length && !_ref.contains(".") && !_alt.contains(".")) { // if
			// another
			// kind
			// of
			// insertion
			// or
			// snv
			// or
			// block
			// substitution
			int numMatchChar = 0;

			for (int i = 0; i < ref.length; i++) { // determines the number of
				// starting matching bases
				// in reference and
				// alternative alleles
				if (ref[i] == alt[i]) {
					numMatchChar++;
				} else {
					break;
				}
			}

			stop = _start + ref.length - numMatchChar; // Variant Stop

			if (numMatchChar == ref.length) { // is an insertion?

				if (numMatchChar > 0) {
					String newRef = "";
					String newAlt = "";

					for (int i = numMatchChar; i < alt.length; i++) {
						newAlt += alt[i];
					}
					for (int i = numMatchChar; i < ref.length; i++) {
						newRef += ref[i];
					}

					if (newRef.equals("")) {
						newRef = ".";
					}

					_ref = newRef;
					_alt = newAlt;

				}
			}

		} else {

			return null;
		}

		String[] output = { Integer.toString(_start), Integer.toString(stop), _ref, _alt };

		return output;
	}

	public static ArrayList<UCSCrefGene> getGeneList() {
		return refGenes;

	}

	public void geneStats() {
		int nullProtienCount = 0;

		int rnaCount = 0;

		for (UCSCrefGene gene : refGenes) {

			if (toRemove.contains(gene.getID())) {
				continue;
			}

			if (gene.getID().startsWith("NR")) {
				rnaCount++;
			} else if (gene.getProtein1() == null) {
				nullProtienCount++;
			}

		}

		System.out.println("refGenes.size() " + refGenes.size());
		System.out.println("nullProtienCount " + nullProtienCount);
		System.out.println("rnaCount " + rnaCount);

	}

	public void filterTransctipts() {
		HashMap<String, HashSet<String>> byName = new HashMap<>();

		int nullProtienCount = 0;

		int rnaCount = 0;

		// byName = <gene name, <gene id>>
		for (UCSCrefGene gene : refGenes) {
			if (byName.containsKey(gene.getName())) {
				byName.get(gene.getName()).add(gene.getID());
			} else {
				HashSet<String> id = new HashSet<>();
				id.add(gene.getID());
				byName.put(gene.getName(), id);
			}

		}

		// when a gene name has multiple id, and have at least
		// one id.startsWith("NM"), add id.startsWith("NR") to toRemove
		// TreeSet <String> toRemove = new TreeSet<>();
		for (String name : byName.keySet()) {
			TreeSet<String> toAdd = new TreeSet<>();
			boolean hasNM = false;
			for (String id : byName.get(name)) {
				if (id.startsWith("NR")) {
					toAdd.add(id);
				} else {
					hasNM = true;
				}
			}
			if (hasNM == true) {
				toRemove.addAll(toAdd);
			}
		}

		// for(int i =0; i<refGenes.size();i++){
		// if(toRemove.contains(refGenes.get(i).getID())){
		// refGenes.remove(i);
		// }
		// }
	}

	public String getCytoBand(int _chrom, int _startPos) {
		return cytoband.getCytoBand(_chrom, _startPos);
	}

	public static String flipStrand(String _str) {
		String flippedcdna;
		char[] flipcdna = _str.toCharArray();
		flippedcdna = "";
		for (int i = flipcdna.length - 1; i >= 0; i--) {
			if (flipcdna[i] == 'C') {
				flippedcdna += "G";
			} else if (flipcdna[i] == 'G') {
				flippedcdna += "C";
			} else if (flipcdna[i] == 'T') {
				flippedcdna += "A";
			} else if (flipcdna[i] == 'A') {
				flippedcdna += "T";
			} else {
				flippedcdna += flipcdna[i];
			}
		}
		return flippedcdna;
	}
}
