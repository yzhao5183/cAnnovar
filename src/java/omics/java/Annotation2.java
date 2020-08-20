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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Max He, PhD; Yiqing Zhao, MS
 */
public class Annotation2 {

	private static Cluster cluster;
	private static Session session;

	private static String[] connectionParameters;

	private static String table;

	private static Cytoband cytoband;

	private static ArrayList<UCSCrefGene2> refGenes;
	private static HashMap<Integer, TreeMap<String, Integer>> refGeneIDX;
	// private static HashMap<Integer, TreeMap<Integer, String>>
	// refGeneStartIDX;

	public static HashMap<String, String> codon1;
	public static HashMap<String, String> codon3;
	public static HashMap<String, String> codon1m;
	public static HashMap<String, String> codon3m;

	TreeSet<String> toRemove = new TreeSet<>();
	String aFunc_refGene = "intergenic";
	String aExonicFunc_refGene = ".";
	public static ArrayList<String[]> refgenes = new ArrayList<String[]>();

	public Annotation2() {

		cytoband = new Cytoband();

		initCodons();

	}

	public Annotation2(String[] _connectionParameters, String _table, String _file) throws IOException {

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
				// initiate a ref gene entry from UCSCrefGene2 file
				UCSCrefGene2 gene = new UCSCrefGene2(sCurrentLine);

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

			UCSCrefGene2 gene = null;

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

	public String getVariantHeaders() {

		Variant variant = new Variant();

		return variant.getHeaders();
	}

	public String annotateVariant(String _variant) {

		Variant variant = new Variant(_variant);

		ArrayList<UCSCrefGene2> genes = getGenes(variant.chr, variant.start, variant.end);

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
			for (UCSCrefGene2 gene : genes) {

				String aFunc_refGene = "intergenic";
				String aExonicFunc_refGene = ".";

				if (gene.getID().startsWith("NR")) {

					// System.out.println("gene.getName() "+ gene.getName());
					if (gene.betweenTX(variant.start, variant.end)) {

						if (gene.isSplice(variant.start, variant.end) >= 0) {
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

					if (gene.isSplice(variant.start, variant.end) >= 0) {
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

	// Select from cassandra||betweenTx&Upstream&Downstream
	public String annotateVariant2(String _variant) throws IOException {

		Variant variant = new Variant(_variant);
		String annotation = getVariantAnnotation(variant.chr, variant.start, variant.end, variant.alt);
		// System.out.println(annotation);
		if (annotation.equals("")) {
			annotation = annotateVariant2_1(_variant);
			uploadSingleVar(annotation);
		}

		return annotation;
	}

	// Select from betweenTx&Upstream&Downstream
	public String annotateVariant2_1(String _variant) {

		Variant variant = new Variant(_variant);
		String hgvsType = getHGVStype(variant.ref, variant.alt);
		// System.out.println(variant.ref + ":" + variant.alt + ":" + hgvsType);
		// long startTime = System.currentTimeMillis();
		ArrayList<String[]> genes = getGenes2_1(variant.chr, variant.start, variant.end);
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("GetGene "+totalTime);
		ArrayList<String> cassandraAnnotations = annotateCassandraFromAVINPUT(variant.chr, variant.cytoband,
				variant.start, variant.end, variant.ref, variant.alt);

		if (cassandraAnnotations.size() != 0) {
			for (String annotation : cassandraAnnotations) {
				if (!annotation.equals("")) {
					variant.setCassandraAnnotations(annotation);
				}
			}
		}

		if (genes.isEmpty()) {
			variant.setFunc_refGene("intergenic");
		} else {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsend(4)|cdsstart(5)|exoncount(6)|exonends(7)|
				// exonstarts(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// protein1(14)|strand(15)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsEnd = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int cdsStart = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int exonCount = Integer.parseInt(gene[6]);
				UCSCrefGene2.setexonCount(exonCount);
				String exonEnds = gene[7].substring(0, gene[7].length() - 1).toUpperCase();
				UCSCrefGene2.setexonEnds(exonEnds);
				String exonStarts = gene[8].substring(0, gene[8].length() - 1).toUpperCase();
				UCSCrefGene2.setexonStarts(exonStarts);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[14].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[15].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				String aGeneDetail_refGene = id.substring(0, id.indexOf(":"));
				String aAAChange_refGene = "";
				int exonNumber = -1;

				String aFunc_refGene = "intergenic";
				String aExonicFunc_refGene = ".";

				if (id.startsWith("NR")) {

					// System.out.println("gene.getName() "+ gene.getName());
					if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

						if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
							aFunc_refGene = "ncRNA_splicing";

						} else if (UCSCrefGene2.inExon(variant.start, variant.end) >= 0) {
							aFunc_refGene = "ncRNA_exonic";

						} else if (UCSCrefGene2.inExon(variant.start, variant.end) < 0) {
							aFunc_refGene = "ncRNA_intronic";
						}
					} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "downstream";
						} else {
							aFunc_refGene = "upstream";

						}
					} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "upstream";
						} else {
							aFunc_refGene = "downstream";
						}

					} else {
						aFunc_refGene = "intergenic";
					}

				} else if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

					if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
						exonNumber = UCSCrefGene2.isSplice(variant.start, variant.end);
						aFunc_refGene = "splicing";
					} else if (UCSCrefGene2.inExon(variant.start, variant.end) < 0) {
						aFunc_refGene = "intronic";
					}
					else if (UCSCrefGene2.in3utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR3";
						} else {
							aFunc_refGene = "UTR5";
						}

					} 
					else if (UCSCrefGene2.in5utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR5";
						} else {
							aFunc_refGene = "UTR3";
						}
					} // Annovar result tag exonic from betweenCDS
					else if (UCSCrefGene2.betweenCDS(variant.start, variant.end)) {
						exonNumber = UCSCrefGene2.inExon(variant.start, variant.end) + 1;
						aFunc_refGene = "exonic";
						aExonicFunc_refGene = UCSCrefGene2.getExonicFunc_refGene(variant.start, variant.end,
								variant.ref, variant.alt);
					} 
				} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {
					if (strand) {
						aFunc_refGene = "downstream";
					} else {
						aFunc_refGene = "upstream";
					}
				} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

					if (strand) {
						aFunc_refGene = "upstream";
					} else {
						aFunc_refGene = "downstream";
					}

				} else {
					aFunc_refGene = "intergenic";
				}

				// annotate gene detail
				if (aFunc_refGene.equals("exonic")) {
					String[] fields1 = exonStarts.split(";");
					String[] fields2 = exonEnds.split(";");
					int[] offsets = new int[exonCount];
					int[] interval = new int[exonCount];
					interval[0] = 0;
					for (int i = 1; i < exonCount; i++) {
						interval[i] = Integer.parseInt(fields1[i]) - Integer.parseInt(fields2[i - 1]);
						// System.out.println(interval[i]);
					}
					int cdsStartExon = UCSCrefGene2.inExon(cdsStart, cdsStart) + 1;
					int cdsEndExon = UCSCrefGene2.inExon(cdsEnd, cdsEnd) + 1;
					// System.out.println(cdsStartExon);System.out.println(cdsEndExon);
					for (int i = 0; i < cdsStartExon; i++) {
						offsets[i] = Integer.parseInt(fields1[i]) - cdsStart;
						if (offsets[i] < 0) {
							offsets[i] = 0;
						}
						// System.out.println(offsets[i]);
					}
					for (int i = cdsStartExon; i < cdsEndExon; i++) {
						offsets[i] = offsets[i - 1] + interval[i];
						// System.out.println(offsets[i]);
					}
					if (cdsEndExon < exonCount) {
					}
					for (int i = cdsEndExon; i < exonCount; i++) {
						// System.out.println(offsets[i]);
					}
					// System.out.println(offsets[exonNumber -1]);
					int startoffset = variant.start - cdsStart - offsets[exonNumber - 1];
					int endoffset = variant.end - cdsStart - offsets[exonNumber - 1];
					int pstartoffset = (int) Math.floor(startoffset / 3) + 1;
					if (startoffset % 3 == 0) {
						pstartoffset = startoffset / 3;
					}
					int pendoffset = (int) Math.floor(endoffset / 3) + 1;
					if (endoffset % 3 == 0) {
						pendoffset = endoffset / 3;
					}

					if (strand) {
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
										+ "c." + startoffset + "del" + variant.ref;

							} else {
								aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
										+ "c." + startoffset + "_" + endoffset + "del" + variant.ref;
							}

						} else if (hgvsType.equals("ins")) {
							endoffset++;
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + startoffset + "_" + endoffset + "ins" + variant.alt;
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + startoffset + "_" + endoffset + "delins" + variant.alt;
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + startoffset + variant.ref + ">" + variant.alt;
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + startoffset + "_" + endoffset + "inv";
						}
						
						// get protein detail
						// System.out.println(pstartoffset);System.out.println(pendoffset);
						if (aExonicFunc_refGene.contains("SNV") || aExonicFunc_refGene.startsWith("stop")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								String p2 = UCSCrefGene2.newProtein.substring(pstartoffset - 1, pendoffset);
								aAAChange_refGene = "p." + p1 + pstartoffset + p2;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.equals("nonframeshift deletion")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								aAAChange_refGene = "p." + pstartoffset + "_" + pendoffset + "del" + p1;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.equals("nonframeshift insertion")) {
							if (!protein1.equals("NULL")) {
								String cdna = mrna.substring(mrnaStartOffset, mrnaEndOffset);
								char[] cdnaArray = cdna.toCharArray();
								char[] cdnaArray2 = new char[cdna.length() + variant.alt.length()];
								char[] refArray = variant.ref.toCharArray();
								char[] altArray = variant.alt.toCharArray();
								int startOffset = UCSCrefGene2.getStartOffsetForVariant(variant.start, variant.end);
								String cdna1 = "";
								String cdna2 = "";
								for (int i = startoffset - 2; i < (startoffset + 2 * variant.alt.length()); i++) {
									cdna1 += cdnaArray[i - 1];
								}
								for (int i = startoffset; i < cdna.length() + 1; i++) {
									cdnaArray2[i + variant.alt.length() - 1] = cdnaArray[i - 1];
								}
								for (int i = startoffset + 1; i < (startoffset + 1 + variant.alt.length()); i++) {
									cdnaArray2[i - 1] = altArray[i - startoffset - 1];
								}
								for (int i = 1; i < startoffset + 1; i++) {
									cdnaArray2[i - 1] = cdnaArray[i - 1];
								}
								for (int i = startoffset - 2; i < (startoffset + 2 * variant.alt.length() + 1); i++) {
									cdna2 += cdnaArray2[i - 1];
								}
								// System.out.println(cdna1+":"+cdna2);
								// System.out.println(startOffset);
								// System.out.println(mrnaStartOffset);
								// System.out.println(startoffset);

								String newCdna = new String(cdnaArray2);

								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								String protein2 = UCSCrefGene2.translateDNA(newCdna);
								// System.out.println(newCdna);System.out.println(cdna);
								// System.out.println(protein1);
								// System.out.println(protein2);
								String p2 = protein2.substring(pstartoffset - 1, pendoffset + variant.alt.length() / 3);
								aAAChange_refGene = "p." + p1 + pstartoffset + "delins" + p2;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.contains("frameshift")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pstartoffset);
								aAAChange_refGene = "p." + p1 + pstartoffset + "fs";
							}
							// System.out.println(aAAChange_refGene);
						}
					} else {
						int cdsStartExon2 = exonCount - cdsEndExon + 1;
						int cdsEndExon2 = exonCount - cdsStartExon + 1;
						// System.out.println(cdsStartExon2);System.out.println(cdsEndExon2);
						for (int i = 0; i < cdsStartExon2; i++) {
							offsets[i] = cdsEnd - Integer.parseInt(fields2[exonCount - i - 1]);
							if (offsets[i] < 0) {
								offsets[i] = 0;
							}
							// System.out.println(offsets[i]);
						}
						for (int i = cdsStartExon2; i < cdsEndExon2; i++) {
							offsets[i] = offsets[i - 1] + interval[exonCount - i];
							// System.out.println(offsets[i]);
						}
						if (cdsEndExon2 < exonCount) {
						}
						for (int i = cdsEndExon2; i < exonCount; i++) {
							// System.out.println(offsets[i]);
						}
						// System.out.println(offsets[exonCount-exonNumber]);
						startoffset = cdsEnd - variant.start + 1 - offsets[exonCount - exonNumber];
						endoffset = cdsEnd - variant.end + 1 - offsets[exonCount - exonNumber];
						exonNumber = exonCount + 1 - exonNumber;
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
										+ "c." + startoffset + "del" + Annotation.flipStrand(variant.ref);
							} else {
								aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
										+ "c." + endoffset + "_" + startoffset + "del"
										+ Annotation.flipStrand(variant.ref);
							}
						} else if (hgvsType.equals("ins")) {
							endoffset--;
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + endoffset + "_" + startoffset + "ins" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + endoffset + "_" + startoffset + "delins"
									+ Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + startoffset + Annotation.flipStrand(variant.ref) + ">"
									+ Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = name + ":" + aGeneDetail_refGene + ":" + "exon" + exonNumber + ":"
									+ "c." + endoffset + "_" + startoffset + "inv";
						}

						// get protein detail
						pstartoffset = (int) Math.floor(endoffset / 3) + 1;
						if (endoffset % 3 == 0) {
							pstartoffset = endoffset / 3;
						}
						pendoffset = (int) Math.floor(startoffset / 3) + 1;
						if (startoffset % 3 == 0) {
							pendoffset = startoffset / 3;
						}
						// System.out.println(pstartoffset);System.out.println(pendoffset);
						if (aExonicFunc_refGene.contains("SNV") || aExonicFunc_refGene.startsWith("stop")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								String p2 = UCSCrefGene2.newProtein.substring(pstartoffset - 1, pendoffset);
								aAAChange_refGene = "p." + p1 + pstartoffset + p2;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.equals("nonframeshift deletion")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								aAAChange_refGene = "p." + pstartoffset + "_" + pendoffset + "del" + p1;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.equals("nonframeshift insertion")) {
							if (!protein1.equals("NULL")) {
								int startOffset = UCSCrefGene2.getStartOffsetForVariant(variant.start, variant.end);
								startOffset = mrna.length() - startOffset;
								// System.out.println(startOffset);System.out.println(mrnaStartOffset);System.out.println(endoffset);
								mrna = Annotation.flipStrand(mrna);
								String cdna = mrna.substring(mrnaStartOffset, mrnaEndOffset);
								char[] cdnaArray = cdna.toCharArray();
								char[] cdnaArray2 = new char[cdna.length() + variant.alt.length()];
								String alt = Annotation.flipStrand(variant.alt);
								char[] refArray = variant.ref.toCharArray();
								char[] altArray = alt.toCharArray();
								String cdna1 = "";
								String cdna2 = "";
								// for (int i = endoffset - 2; i < (endoffset +
								// 2 * alt.length()); i++) {
								// System.out.println(i);
								// cdna1 += cdnaArray[i - 1];
								// }
								for (int i = endoffset; i < cdna.length() + 1; i++) {
									cdnaArray2[i + alt.length() - 1] = cdnaArray[i - 1];
								}
								for (int i = endoffset + 1; i < (endoffset + 1 + alt.length()); i++) {
									cdnaArray2[i - 1] = altArray[i - endoffset - 1];
								}
								for (int i = 1; i < endoffset + 1; i++) {
									cdnaArray2[i - 1] = cdnaArray[i - 1];
								}
								// for (int i = endoffset - 2; i < (endoffset +
								// 2 * alt.length() + 1); i++) {
								// cdna2 += cdnaArray2[i - 1];
								// }
								// System.out.println(cdna1 + ":" + cdna2);

								String newCdna = new String(cdnaArray2);

								String p1 = protein1.substring(pstartoffset - 1, pendoffset);
								protein1 = UCSCrefGene2.translateDNA(cdna);
								String protein2 = UCSCrefGene2.translateDNA(newCdna);
								// System.out.println(cdna);System.out.println(newCdna);
								// System.out.println(protein1);System.out.println(protein2);
								String p2 = protein2.substring(pstartoffset - 1, pendoffset + variant.alt.length() / 3);
								aAAChange_refGene = "p." + p1 + pstartoffset + "delins" + p2;
							}
							// System.out.println(aAAChange_refGene);
						} else if (aExonicFunc_refGene.contains("frameshift")) {
							if (!protein1.equals("NULL")) {
								String p1 = protein1.substring(pstartoffset - 1, pstartoffset);
								aAAChange_refGene = "p." + p1 + pstartoffset + "fs";
							}
							// System.out.println(aAAChange_refGene);
						}
					}
					aGeneDetail_refGene += ":" + aAAChange_refGene;
					// System.out.println("exonic:" + aGeneDetail_refGene);
				} else if (aFunc_refGene.equals("splicing")) {
					String[] fields1 = exonStarts.split(";");
					String[] fields2 = exonEnds.split(";");
					int[] offsets = new int[exonCount];
					int[] interval = new int[exonCount];
					interval[0] = 0;
					for (int i = 1; i < exonCount; i++) {
						interval[i] = Integer.parseInt(fields1[i]) - Integer.parseInt(fields2[i - 1]);
						// System.out.println(interval[i]);
					}
					int cdsStartExon = UCSCrefGene2.inExon(cdsStart, cdsStart) + 1;
					int cdsEndExon = UCSCrefGene2.inExon(cdsEnd, cdsEnd) + 1;
					// System.out.println(cdsStartExon);System.out.println(cdsEndExon);
					for (int i = 0; i < cdsStartExon; i++) {
						offsets[i] = Integer.parseInt(fields1[i]) - cdsStart;
						if (offsets[i] < 0) {
							offsets[i] = 0;
						}
						// System.out.println(offsets[i]);
					}
					for (int i = cdsStartExon; i < cdsEndExon; i++) {
						offsets[i] = offsets[i - 1] + interval[i];
						// System.out.println(offsets[i]);
					}
					if (cdsEndExon < exonCount) {
					}
					for (int i = cdsEndExon; i < exonCount; i++) {
						// System.out.println(offsets[i]);
					}
					// System.out.println(offsets[exonNumber -1]);
					int startoffset = variant.start - cdsStart - offsets[exonNumber - 1];
					int endoffset = variant.end - cdsStart - offsets[exonNumber - 1];
					int startchange = variant.start - Integer.parseInt(fields1[exonNumber - 1]) - 1;
					int endchange = variant.end - Integer.parseInt(fields2[exonNumber - 1]);
					int exonNumber2 = exonNumber + 1;
					int startchange2 = -1;
					int endchange2 = -1;

					if (exonNumber != exonCount) {
						startchange2 = variant.start - Integer.parseInt(fields1[exonNumber2 - 1]) - 1;
					}
					if (exonNumber != 1) {
						exonNumber2 = exonNumber - 1;
						endchange2 = variant.end - Integer.parseInt(fields2[exonNumber2 - 1]);
					}

					if (strand) {
						if (startchange <= 2 && startchange > 0) {
							startoffset = startoffset - startchange;
							if (startoffset >= 0) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
										+ startoffset + "+" + startchange + variant.ref + ">" + variant.alt;
							} else {
								aGeneDetail_refGene = ".";
							}
						} else if (startchange >= -2 && startchange < 0) {
							if (exonNumber != 1 && endchange2 <= 2 && endchange2 > 0) {
								startoffset = startoffset - startchange;
								int startoffset2 = startoffset - endchange2;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + startchange + variant.ref + ">" + variant.alt + "|"
											+ aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c." + startoffset2
											+ "+" + endchange2 + variant.ref + ">" + variant.alt;
								} else {
									aGeneDetail_refGene = ".";
								}
							} else {
								startoffset = startoffset - startchange;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + startchange + variant.ref + ">" + variant.alt;
								} else {
									aGeneDetail_refGene = ".";
								}
							}
						} else if (endchange >= -2 && endchange < 0) {
							startoffset = startoffset - endchange;
							if (startoffset >= 0) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
										+ startoffset + endchange + variant.ref + ">" + variant.alt;
							} else {
								aGeneDetail_refGene = ".";
							}
						} else if (endchange <= 2 && endchange > 0) {
							if (exonNumber != exonCount && startchange2 >= -2 && startchange2 < 0) {
								startoffset = startoffset - endchange;
								int startoffset2 = startoffset - startchange2;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + "+" + endchange + variant.ref + ">" + variant.alt + "|"
											+ aGeneDetail_refGene + ":" + "exon" + exonNumber2 + ":c." + startoffset2
											+ startchange2 + variant.ref + ">" + variant.alt;
								} else {
									aGeneDetail_refGene = ".";
								}
							} else {
								startoffset = startoffset - endchange;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + "+" + endchange + variant.ref + ">" + variant.alt;
								} else {
									aGeneDetail_refGene = ".";
								}
							}
						}
					} else {
						int cdsStartExon2 = exonCount - cdsEndExon + 1;
						int cdsEndExon2 = exonCount - cdsStartExon + 1;
						// System.out.println(cdsStartExon2);System.out.println(cdsEndExon2);
						for (int i = 0; i < cdsStartExon2; i++) {
							offsets[i] = cdsEnd - Integer.parseInt(fields2[exonCount - i - 1]);
							if (offsets[i] < 0) {
								offsets[i] = 0;
							}
							// System.out.println(offsets[i]);
						}
						for (int i = cdsStartExon2; i < cdsEndExon2; i++) {
							offsets[i] = offsets[i - 1] + interval[exonCount - i];
							// System.out.println(offsets[i]);
						}
						if (cdsEndExon2 < exonCount) {
						}
						for (int i = cdsEndExon2; i < exonCount; i++) {
							// System.out.println(offsets[i]);
						}
						// System.out.println(offsets[exonCount-exonNumber]);
						startoffset = cdsEnd - variant.start + 1 - offsets[exonCount - exonNumber];
						endoffset = cdsEnd - variant.end + 1 - offsets[exonCount - exonNumber];
						startchange = Integer.parseInt(fields1[exonNumber - 1]) - variant.start + 1;
						endchange = Integer.parseInt(fields2[exonNumber - 1]) - variant.end;
						if (exonNumber != exonCount) {
							startchange2 = Integer.parseInt(fields1[exonNumber2 - 1]) - variant.start + 1;
						}
						if (exonNumber != 1) {
							endchange2 = Integer.parseInt(fields2[exonNumber2 - 1]) - variant.end;
						}
						exonNumber = exonCount + 1 - exonNumber;

						if (startchange <= 2 && startchange > 0) {
							startoffset = startoffset - startchange;
							if (startoffset >= 0) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
										+ startoffset + "+" + startchange + Annotation.flipStrand(variant.ref) + ">"
										+ Annotation.flipStrand(variant.alt);
							} else {
								aGeneDetail_refGene = ".";
							}
						} else if (startchange >= -2 && startchange < 0) {
							if (exonNumber != 1 && endchange2 <= 2 && endchange2 > 0) {
								startoffset = startoffset - startchange;
								int startoffset2 = startoffset - endchange2;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + startchange + Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt) + "|" + aGeneDetail_refGene + ":"
											+ "exon" + exonNumber + ":c." + startoffset2 + "+" + endchange2
											+ Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt);
								} else {
									aGeneDetail_refGene = ".";
								}
							} else {
								startoffset = startoffset - startchange;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + startchange + Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt);
								} else {
									aGeneDetail_refGene = ".";
								}
							}
						} else if (endchange >= -2 && endchange < 0) {
							startoffset = startoffset - endchange;
							if (startoffset >= 0) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
										+ startoffset + endchange + Annotation.flipStrand(variant.ref) + ">"
										+ Annotation.flipStrand(variant.alt);
							} else {
								aGeneDetail_refGene = ".";
							}
						} else if (endchange <= 2 && endchange > 0) {
							if (exonNumber != exonCount && startchange2 >= -2 && startchange2 < 0) {
								startoffset = startoffset - endchange;
								int startoffset2 = startoffset - startchange2;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + "+" + endchange + Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt) + "|" + aGeneDetail_refGene + ":"
											+ "exon" + exonNumber2 + ":c." + startoffset2 + startchange2
											+ Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt);
								} else {
									aGeneDetail_refGene = ".";
								}
							} else {
								startoffset = startoffset - endchange;
								if (startoffset >= 0) {
									aGeneDetail_refGene = aGeneDetail_refGene + ":" + "exon" + exonNumber + ":c."
											+ startoffset + "+" + endchange + Annotation.flipStrand(variant.ref) + ">"
											+ Annotation.flipStrand(variant.alt);
								} else {
									aGeneDetail_refGene = ".";
								}
							}
						}
					}
					// System.out.println("splicing:" + aGeneDetail_refGene);
				} else if (aFunc_refGene.equals("UTR3")) {
					int startoffset = variant.start - cdsEnd;
					int endoffset = variant.end - cdsEnd;
					if (strand) {
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "del"
										+ variant.ref;
							} else {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "_*" + endoffset
										+ "del" + variant.ref;
							}
						} else if (hgvsType.equals("ins")) {
							endoffset++;
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "_*" + endoffset
									+ "ins" + variant.alt;
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "_*" + endoffset
									+ "delins" + variant.alt;
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + variant.ref + ">"
									+ variant.alt;
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "_*" + endoffset
									+ "inv";
						}
					} else {
						startoffset = cdsStart - variant.start + 1;
						endoffset = cdsStart - variant.end + 1;
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset + "del"
										+ variant.ref;
							} else {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + endoffset + "_*" + startoffset
										+ "del" + variant.ref;
							}
						} else if (hgvsType.equals("ins")) {
							endoffset--;
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + endoffset + "_*" + startoffset
									+ "ins" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + endoffset + "_*" + startoffset
									+ "delins" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + startoffset
									+ Annotation.flipStrand(variant.ref) + ">" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c.*" + endoffset + "_*" + startoffset
									+ "inv";
						}
					}
					// System.out.println("UTR3:" + aGeneDetail_refGene);
				} else if (aFunc_refGene.equals("UTR5")) {
					int startoffset = variant.start - cdsStart - 1;
					int endoffset = variant.end - cdsStart - 1;
					if (strand) {
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "del"
										+ variant.ref;
							} else {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "_" + endoffset
										+ "del" + variant.ref;
							}

						} else if (hgvsType.equals("ins")) {
							endoffset++;
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "_" + endoffset
									+ "ins" + variant.alt;
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "_" + endoffset
									+ "delins" + variant.alt;
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + variant.ref + ">"
									+ variant.alt;
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "_" + endoffset
									+ "inv";
						}
					} else {
						startoffset = cdsEnd - variant.start;
						endoffset = cdsEnd - variant.end;
						if (hgvsType.equals("del")) {
							if (variant.start == variant.end) {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset + "del"
										+ Annotation.flipStrand(variant.ref);
							} else {
								aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + endoffset + "_" + startoffset
										+ "del" + Annotation.flipStrand(variant.ref);
							}
						} else if (hgvsType.equals("ins")) {
							endoffset--;
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + +endoffset + "_" + startoffset
									+ "ins" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("delins")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + +endoffset + "_" + startoffset
									+ "delins" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("sub")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + startoffset
									+ Annotation.flipStrand(variant.ref) + ">" + Annotation.flipStrand(variant.alt);
						} else if (hgvsType.equals("inv")) {
							aGeneDetail_refGene = aGeneDetail_refGene + ":" + "c." + +endoffset + "_" + startoffset
									+ "inv";
						}
					}
					// System.out.println("UTR5:" + aGeneDetail_refGene);
				}

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(aGeneDetail_refGene);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}

		}

		return variant.getAnnotations();
	}

	// Select from betweenTx||Upstream||Downstream
	public String annotateVariant2_2(String _variant) {

		Variant variant = new Variant(_variant);
		// long startTime = System.currentTimeMillis();
		ArrayList<String[]> genes = getGenes2_2(variant.chr, variant.start, variant.end);
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("GetGene "+totalTime);
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
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(2)|cdsend(4)|cdsstart(5)|exoncount(6)|exonends(7)|
				// exonstarts(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// protein1(14)|strand(15)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsEnd = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int cdsStart = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int exonCount = Integer.parseInt(gene[6]);
				UCSCrefGene2.setexonCount(exonCount);
				String exonEnds = gene[7].substring(0, gene[7].length() - 1).toUpperCase();
				UCSCrefGene2.setexonEnds(exonEnds);
				String exonStarts = gene[8].substring(0, gene[8].length() - 1).toUpperCase();
				UCSCrefGene2.setexonStarts(exonStarts);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[14].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[15].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				String aFunc_refGene = "intergenic";
				String aExonicFunc_refGene = ".";

				if (id.startsWith("NR")) {

					// System.out.println("gene.getName() "+ gene.getName());
					if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

						if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
							aFunc_refGene = "ncRNA_splicing";

						} else if (UCSCrefGene2.inExon(variant.start, variant.end) >= 0) {
							aFunc_refGene = "ncRNA_exonic";

						} else if (UCSCrefGene2.inExon(variant.start, variant.end) < 0) {
							aFunc_refGene = "ncRNA_intronic";
						}
					} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "downstream";
						} else {
							aFunc_refGene = "upstream";

						}
					} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "upstream";
						} else {
							aFunc_refGene = "downstream";
						}

					} else {
						aFunc_refGene = "intergenic";
					}

				} else if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

					if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
						aFunc_refGene = "splicing";
					} else if (UCSCrefGene2.inExon(variant.start, variant.end) == -1) {

						aFunc_refGene = "intronic";

						// Annovar result tag exonic from betweenCDS
					} else if (UCSCrefGene2.betweenCDS(variant.start, variant.end)) {
						aFunc_refGene = "exonic";
						aExonicFunc_refGene = UCSCrefGene2.getExonicFunc_refGene(variant.start, variant.end,
								variant.ref, variant.alt);
					} else if (UCSCrefGene2.in3utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR3";
						} else {
							aFunc_refGene = "UTR5";
						}

					} else if (UCSCrefGene2.in5utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR5";
						} else {
							aFunc_refGene = "UTR3";
						}
					}

				} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {
					if (strand) {
						aFunc_refGene = "downstream";
					} else {
						aFunc_refGene = "upstream";
					}
				} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

					if (strand) {
						aFunc_refGene = "upstream";
					} else {
						aFunc_refGene = "downstream";
					}

				} else {
					aFunc_refGene = "intergenic";
				}

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id.substring(0, id.indexOf(":")));
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}

		}

		return variant.getAnnotations();
	}

	// Select from Exon||Splicing||UTR3/UTR5/Intron||UP/Downstream
	public String annotateVariant3(String _variant) {

		Variant variant = new Variant(_variant);

		aFunc_refGene = "intergenic";
		aExonicFunc_refGene = ".";

		ArrayList<String[]> genes = getGenes3(variant.chr, variant.start, variant.end);

		ArrayList<String> cassandraAnnotations = annotateCassandraFromAVINPUT(variant.chr, variant.cytoband,
				variant.start, variant.end, variant.ref, variant.alt);

		if (!cassandraAnnotations.isEmpty()) {
			for (String annotation : cassandraAnnotations) {
				variant.setCassandraAnnotations(annotation);
			}
		}

		if (genes.isEmpty()) {
			variant.setFunc_refGene("intergenic");
		} else if (aFunc_refGene.equals("exonic")) {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsstart(4)|cdsend(5)|exonends(6)|exonstarts(7)|
				// exoncount(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// nonCoding(14)|protein1(15)|strand(16)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsStart = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int cdsEnd = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int exonCount = Integer.parseInt(gene[8]);
				UCSCrefGene2.setexonCount(exonCount);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[15].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[16].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				aExonicFunc_refGene = UCSCrefGene2.getExonicFunc_refGene(variant.start, variant.end, variant.ref,
						variant.alt);

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}
		} else if (aFunc_refGene.equals("splicing")) {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsstart(4)|cdsend(5)|exonends(6)|exonstarts(7)|
				// exoncount(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// nonCoding(14)|protein1(15)|strand(16)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsStart = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int cdsEnd = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int exonCount = Integer.parseInt(gene[8]);
				UCSCrefGene2.setexonCount(exonCount);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[15].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[16].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}
		} else if (aFunc_refGene.equals("ncRNA_exonic")) {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsstart(4)|cdsend(5)|exonends(6)|exonstarts(7)|
				// exoncount(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// nonCoding(14)|protein1(15)|strand(16)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsStart = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int cdsEnd = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int exonCount = Integer.parseInt(gene[8]);
				UCSCrefGene2.setexonCount(exonCount);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[15].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[16].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}
		} else if (aFunc_refGene.equals("ncRNA_splicing")) {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsstart(4)|cdsend(5)|exonends(6)|exonstarts(7)|
				// exoncount(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// nonCoding(14)|protein1(15)|strand(16)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsStart = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int cdsEnd = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int exonCount = Integer.parseInt(gene[8]);
				UCSCrefGene2.setexonCount(exonCount);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[15].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[16].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}
		} else {
			for (String[] gene : genes) {

				// id(0)|chrom(1)|txstart(2)|txend(3)|cdsstart(4)|cdsend(5)|exonends(6)|exonstarts(7)|
				// exoncount(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
				// nonCoding(14)|protein1(15)|strand(16)
				String id = gene[0].toUpperCase();
				UCSCrefGene2.setID(id.toUpperCase());
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);
				int cdsStart = Integer.parseInt(gene[4]);
				UCSCrefGene2.setcdsStart(cdsStart);
				int cdsEnd = Integer.parseInt(gene[5]);
				UCSCrefGene2.setcdsEnd(cdsEnd);
				int exonCount = Integer.parseInt(gene[8]);
				UCSCrefGene2.setexonCount(exonCount);
				String mrna = gene[10].toUpperCase();
				UCSCrefGene2.setMrna(mrna);
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
				String name = gene[13].toUpperCase();
				UCSCrefGene2.setName(name);
				String protein1 = gene[15].toUpperCase();
				UCSCrefGene2.setProtein1(protein1);
				boolean strand = gene[16].toLowerCase().equals("true");
				UCSCrefGene2.setStrand(strand);

				if (id.startsWith("NR")) {

					// System.out.println("gene.getName() "+ gene.getName());
					if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

						aFunc_refGene = "ncRNA_intronic";

					} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "downstream";
						} else {
							aFunc_refGene = "upstream";

						}
					} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

						if (strand) {
							aFunc_refGene = "upstream";
						} else {
							aFunc_refGene = "downstream";
						}

					} else {
						aFunc_refGene = "intergenic";
					}

				} else if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

					if (UCSCrefGene2.in3utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR3";
						} else {
							aFunc_refGene = "UTR5";
						}

					} else if (UCSCrefGene2.in5utr(variant.start, variant.end)) {
						if (strand) {
							aFunc_refGene = "UTR5";
						} else {
							aFunc_refGene = "UTR3";
						}
					} else {
						aFunc_refGene = "intronic";
					}

				} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {
					if (strand) {
						aFunc_refGene = "downstream";
					} else {
						aFunc_refGene = "upstream";
					}
				} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

					if (strand) {
						aFunc_refGene = "upstream";
					} else {
						aFunc_refGene = "downstream";
					}

				} else {
					aFunc_refGene = "intergenic";
				}

				variant.setGene_refGene(name);
				variant.setGeneDetail_refGene(id);
				variant.setFunc_refGene(aFunc_refGene);
				variant.setExonicFunc_refGene(aExonicFunc_refGene);
			}

		}

		return variant.getAnnotations();
	}

	// Select from chrom = currentChrom/*
	public String annotateVariant4(String _variant) {

		Variant variant = new Variant(_variant);

		ArrayList<String> cassandraAnnotations = annotateCassandraFromAVINPUT(variant.chr, variant.cytoband,
				variant.start, variant.end, variant.ref, variant.alt);
		long startTime = System.currentTimeMillis();
		ArrayList<String[]> genes = getGenes4_2(variant.chr, variant.start, variant.end);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("GetGene " + totalTime);

		startTime = System.currentTimeMillis();
		if (!cassandraAnnotations.isEmpty()) {
			for (String annotation : cassandraAnnotations) {
				variant.setCassandraAnnotations(annotation);
			}
		}
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("GetFreq " + totalTime);

		for (String[] gene : genes) {

			// id(0)|chrom(1)|txstart(2)|txend(2)|cdsend(4)|cdsstart(5)|exoncount(6)|exonends(7)|
			// exonstarts(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
			// protein1(14)|strand(15)
			String id = gene[0].toUpperCase();
			UCSCrefGene2.setID(id.toUpperCase());
			int txStart = Integer.parseInt(gene[2]);
			UCSCrefGene2.setTXstart(txStart);
			int txEnd = Integer.parseInt(gene[3]);
			UCSCrefGene2.setTXend(txEnd);
			int cdsEnd = Integer.parseInt(gene[4]);
			UCSCrefGene2.setcdsEnd(cdsEnd);
			int cdsStart = Integer.parseInt(gene[5]);
			UCSCrefGene2.setcdsStart(cdsStart);
			int exonCount = Integer.parseInt(gene[6]);
			UCSCrefGene2.setexonCount(exonCount);
			String exonEnds = gene[7].substring(0, gene[7].length() - 1).toUpperCase();
			UCSCrefGene2.setexonEnds(exonEnds);
			String exonStarts = gene[8].substring(0, gene[8].length() - 1).toUpperCase();
			UCSCrefGene2.setexonStarts(exonStarts);
			String mrna = gene[10].toUpperCase();
			UCSCrefGene2.setMrna(mrna);
			if (!gene[11].equals("null")) {
				int mrnaEndOffset = Integer.parseInt(gene[11]);
				UCSCrefGene2.setmrnaEndOffset(mrnaEndOffset);
			} else {
				System.out.println(id);
				UCSCrefGene2.setmrnaEndOffset(0);
			}
			if (!gene[12].equals("null")) {
				int mrnaStartOffset = Integer.parseInt(gene[12]);
				UCSCrefGene2.setmrnaStartOffset(mrnaStartOffset);
			} else {
				UCSCrefGene2.setmrnaStartOffset(0);
			}
			String name = gene[13].toUpperCase();
			UCSCrefGene2.setName(name);
			String protein1 = gene[14].toUpperCase();
			UCSCrefGene2.setProtein1(protein1);
			boolean strand = gene[15].toLowerCase().equals("true");
			UCSCrefGene2.setStrand(strand);

			String aFunc_refGene = "intergenic";
			String aExonicFunc_refGene = ".";

			if (id.startsWith("NR")) {

				// System.out.println("gene.getName() "+ gene.getName());
				if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

					if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
						aFunc_refGene = "ncRNA_splicing";

					} else if (UCSCrefGene2.inExon(variant.start, variant.end) >= 0) {
						aFunc_refGene = "ncRNA_exonic";

					} else if (UCSCrefGene2.inExon(variant.start, variant.end) < 0) {
						aFunc_refGene = "ncRNA_intronic";
					}
				} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {

					if (strand) {
						aFunc_refGene = "downstream";
					} else {
						aFunc_refGene = "upstream";

					}
				} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

					if (strand) {
						aFunc_refGene = "upstream";
					} else {
						aFunc_refGene = "downstream";
					}

				} else {
					aFunc_refGene = "intergenic";
				}

			} else if (UCSCrefGene2.betweenTX(variant.start, variant.end)) {

				if (UCSCrefGene2.isSplice(variant.start, variant.end) >= 0) {
					aFunc_refGene = "splicing";
				} else if (UCSCrefGene2.inExon(variant.start, variant.end) == -1) {

					aFunc_refGene = "intronic";

					// Annovar result tag exonic from betweenCDS
				} else if (UCSCrefGene2.betweenCDS(variant.start, variant.end)) {
					aFunc_refGene = "exonic";
					aExonicFunc_refGene = UCSCrefGene2.getExonicFunc_refGene(variant.start, variant.end, variant.ref,
							variant.alt);
				} else if (UCSCrefGene2.in3utr(variant.start, variant.end)) {
					if (strand) {
						aFunc_refGene = "UTR3";
					} else {
						aFunc_refGene = "UTR5";
					}

				} else if (UCSCrefGene2.in5utr(variant.start, variant.end)) {
					if (strand) {
						aFunc_refGene = "UTR5";
					} else {
						aFunc_refGene = "UTR3";
					}
				}

			} else if (UCSCrefGene2.isDownStream(variant.start, variant.end)) {
				if (strand) {
					aFunc_refGene = "downstream";
				} else {
					aFunc_refGene = "upstream";
				}
			} else if (UCSCrefGene2.isUpStream(variant.start, variant.end)) {

				if (strand) {
					aFunc_refGene = "upstream";
				} else {
					aFunc_refGene = "downstream";
				}

			} else {
				aFunc_refGene = "intergenic";
			}

			variant.setGene_refGene(name);
			variant.setGeneDetail_refGene(id.substring(0, id.indexOf(":")));
			variant.setFunc_refGene(aFunc_refGene);
			variant.setExonicFunc_refGene(aExonicFunc_refGene);
		}

		return variant.getAnnotations();
	}

	public ArrayList<UCSCrefGene2> getGenes(int _chrom, int _start, int _end) {

		TreeMap<String, Integer> chrom = refGeneIDX.get(_chrom);

		ArrayList<UCSCrefGene2> genes = new ArrayList<>();

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

	// Select from betweenTx&Upstream&Downstream
	public String getVariantAnnotation(int _chrom, int _start, int _end, String _alt) {

		String variantID = _chrom + ":" + _start + "_" + _end + ":" + _alt;
		Statement select = QueryBuilder.select().all().from(connectionParameters[2], "variants")
				.where(eq("variantID", variantID));
		// String select = "SELECT * FROM variants"
		// + " WHERE variantID = '" + variantID +"'"
		// + " ALLOW FILTERING;";
		// System.out.println(select);

		ResultSet rs = session.execute(select);

		String annotation = "";
		if (!rs.toString().startsWith("ResultSet[ exhausted: true")) {
			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());

				String[] vars = results.split(", ");
				// variantid (0)| alt (1)| cadd_phred (2)| cadd_raw (3)| cg69  (4)| chrom (5)| clinsig (6)|
				// clnacc (7)| clndbn (8)| clndsdb (9)| clndsdbid (10)| cosmic70 (11)| cytoband (12)| dann_score (13)| 
				// dgvmerged (14)| eigen (15)| esp6500siv2_all (16)| exac_afr (17)| exac_all (18)| exac_amr (19)| 
				// exac_eas (20)| exac_fin (21)| exac_nfe (22)| exac_oth (23)| exac_sas (24)| exonicfunc_refgene (25)| 
				// fathmm_coding (26)| fathmm_mkl_coding_pred (27)| fathmm_mkl_coding_score (28)| fathmm_noncoding (29)|
				// fathmm_pred (30)| fathmm_score (31)| func_refgene (32)| g1000g2015aug_afr (33)| g1000g2015aug_all (34)|
				// g1000g2015aug_amr (35)| g1000g2015aug_eas (36)| g1000g2015aug_eur (37)| g1000g2015aug_sas (38)| 
				// gene_refgene (39)| genedetail_refgene (40)| genomicsuperdups (41)| gerp_rs (42)| gerpgt2 (43)| 
				// gwascatalog (44)| gwava_region_score (45)| gwava_tss_score (46)| gwava_unmatched_score (47)| 
				// integrated_confidence_value (48)| integrated_fitcons_score (49)| kaviar_ac (50)| kaviar_af (51)| 
				// kaviar_an (52)| lrt_pred (53)| lrt_score (54)| metalr_pred (55)| metalr_score (56)| metasvm_pred (57)| 
				// metasvm_score (58)| mutationassessor_pred (59)| mutationassessor_score (60)| mutationtaster_pred (61)| 
				// mutationtaster_score (62)| nci60 (63)| phastcons20way_mammalian (64)| phastcons7way_vertebrate (65)| 
				// phastconselements46way (66)| phylop20way_mammalian (67)| phylop7way_vertebrate (68)| 
				// polyphen2_hdiv_pred (69)| polyphen2_hdiv_score (70)| polyphen2_hvar_pred (71)| polyphen2_hvar_score (72)| 
				// provean_pred (73)| provean_score (74)| ref (75)| sift_pred (76)| sift_score (77)| siphy_29way_logodds (78)| 
				// snp138  (79)| start (80)| stop (81)| targetscans (82)| tfbsconssites (83)| vest3_score (84)| wgrna(85)

				annotation = vars[5] + "\t" + vars[12] + "\t" + vars[80] + "\t" + vars[81] + "\t"
						+vars[75].toUpperCase() + "\t" + vars[1].toUpperCase() + "\t" + vars[32].replace("rna", "RNA").replace("utr", "UTR")
						+ "\t" + vars[39].toUpperCase() + "\t" + vars[40].toUpperCase() + "\t" + vars[25].replace("snv", "SNV") 
						+ "\t" + vars[2] + "\t" + vars[3] + "\t" + vars[4] + "\t" + vars[6] 
						+ "\t" + vars[7] + "\t" + vars[8] + "\t" + vars[9] + "\t" + vars[10] + "\t" 
						+ vars[11] + "\t" + vars[13] + "\t" + vars[15] + "\t" + vars[16] + "\t" + vars[17] + "\t" 
						+ vars[18] + "\t" + vars[19] + "\t" + vars[20] + "\t" + vars[21] + "\t" + vars[22] + "\t" 
						+ vars[23] + "\t" + vars[24] + "\t" + vars[26] + "\t" + vars[27] + "\t" + vars[28] + "\t" 
						+ vars[29] + "\t" + vars[30] + "\t" + vars[31] + "\t" + vars[33] + "\t" 
						+ vars[34] + "\t" + vars[35] + "\t" + vars[36] + "\t" + vars[37] + "\t" + vars[38] 
						+ "\t" + vars[42] + "\t" + vars[43]  + "\t" + vars[44] + "\t" + vars[45] + "\t" + vars[46] + "\t" 
						+ vars[47] + "\t" + vars[48] + "\t" + vars[49] + "\t" + vars[50] + "\t" + vars[51] + "\t" 
						+ vars[52] + "\t" + vars[53] + "\t" + vars[54] + "\t" + vars[55] + "\t" + vars[56] + "\t" 
						+ vars[57] + "\t" + vars[58] + "\t" + vars[59] + "\t" + vars[60] + "\t" + vars[61] + "\t" 
						+ vars[62] + "\t" + vars[63] + "\t" + vars[64] + "\t" + vars[65] + "\t" + vars[67] + "\t" 
						+ vars[68] + "\t" + vars[69] + "\t" + vars[70] + "\t" + vars[71] + "\t" 
						+ vars[72] + "\t" + vars[73] + "\t" + vars[74] + "\t" + vars[76] + "\t" + vars[77] + "\t" 
						+ vars[78] + "\t" + vars[79] + "\t" + vars[84] + "\t" + vars[66] + "\t" + vars[83] + "\t" 
						+ vars[85] + "\t" + vars[82] + "\t" + vars[41] + "\t" + vars[14];

			}
		}
		return annotation;
	}

	// Select from betweenTx&Upstream&Downstream
	public ArrayList<String[]> getGenes2_1(int _chrom, int _start, int _end) {

		ResultSet rs;
		ArrayList<String[]> genes = new ArrayList<>();

		String select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.betweenTX_cql(_start, _end)
				+ " ALLOW FILTERING;";
		// System.out.println(select);
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				genes.add(fields);
			}

		}

		select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.isUpStream_cql(_start, _end)
				+ " ALLOW FILTERING;";
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				genes.add(fields);
			}

		}

		select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.isDownStream_cql(_start, _end)
				+ " ALLOW FILTERING;";
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				genes.add(fields);
			}

		}
		return genes;

	}

	// Select from betweenTx||Upstream||Downstream
	public ArrayList<String[]> getGenes2_2(int _chrom, int _start, int _end) {

		ResultSet rs;

		ArrayList<String[]> genes = new ArrayList<>();
		// long startTime = System.currentTimeMillis();
		String select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.betweenTX_cql(_start, _end)
				+ " ALLOW FILTERING;";
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				genes.add(fields);
			}

		}
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("Bet"+totalTime);
		if (genes.size() == 0) {
			// startTime = System.currentTimeMillis();
			select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.isUpStream_cql(_start, _end)
					+ " ALLOW FILTERING;";
			rs = session.execute(select);

			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());
				// System.out.println(results);
				String[] fields = results.split(", ");
				if (fields[9].toLowerCase().equals("true")) {
					continue;
				} else {
					genes.add(fields);
				}

			}
			// endTime = System.currentTimeMillis();
			// totalTime = endTime - startTime;
			// System.out.println("U"+totalTime);
		}
		if (genes.size() == 0) {
			// startTime = System.currentTimeMillis();
			select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + UCSCrefGene2.isDownStream_cql(_start, _end)
					+ " ALLOW FILTERING;";
			rs = session.execute(select);

			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());
				// System.out.println(results);
				String[] fields = results.split(", ");
				if (fields[9].toLowerCase().equals("true")) {
					continue;
				} else {
					genes.add(fields);
				}

			}
			// endTime = System.currentTimeMillis();
			// totalTime = endTime - startTime;
			// System.out.println("D"+totalTime);
		}

		return genes;

	}

	// Select from Exon||Splicing||UTR3/UTR5/Intron||UP/Downstream
	public ArrayList<String[]> getGenes3(int _chrom, int _start, int _end) {

		ResultSet rs;

		ArrayList<String[]> genes = new ArrayList<>();

		// Exonic
		long startTime = System.currentTimeMillis();
		String select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
				+ UCSCrefGene2.betweenCDS_cql(_start, _end) + UCSCrefGene2.inExon_cql(_start, _end)
				+ " AND nonCoding = false" + " ALLOW FILTERING;";
		// System.out.println(select);
		rs = session.execute(select);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Exonic " + totalTime);
		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				genes.add(fields);
			}

		}
		endTime = System.currentTimeMillis();
		totalTime = endTime - startTime;
		System.out.println("Exonic " + totalTime);

		// Splicing
		if (genes.size() != 0) {
			aFunc_refGene = "exonic";
		} else {
			startTime = System.currentTimeMillis();
			select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom + UCSCrefGene2.betweenTX_cql(_start, _end)
					+ UCSCrefGene2.isSplice_cql1(_start, _end) + " AND nonCoding = false" + " ALLOW FILTERING;";
			// System.out.println(select);
			rs = session.execute(select);

			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());
				// System.out.println(results);
				String[] fields = results.split(", ");
				if (fields[9].toLowerCase().equals("true")) {
					continue;
				} else {
					genes.add(fields);
				}

			}
			select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom + UCSCrefGene2.betweenTX_cql(_start, _end)
					+ UCSCrefGene2.isSplice_cql2(_start, _end) + " AND nonCoding = false" + " ALLOW FILTERING;";
			// System.out.println(select);
			rs = session.execute(select);

			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());
				// System.out.println(results);
				String[] fields = results.split(", ");
				if (fields[9].toLowerCase().equals("true")) {
					continue;
				} else {
					genes.add(fields);
				}

			}
			endTime = System.currentTimeMillis();
			totalTime = endTime - startTime;
			System.out.println("Splicing " + totalTime);

			// nc_Exonic
			if (genes.size() != 0) {
				aFunc_refGene = "splicing";
			} else {
				startTime = System.currentTimeMillis();
				select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
						+ UCSCrefGene2.betweenTX_cql(_start, _end) + UCSCrefGene2.inExon_cql(_start, _end)
						+ " AND nonCoding = true" + " ALLOW FILTERING;";
				// System.out.println(select);
				rs = session.execute(select);

				for (Row row : rs) {
					String results = Variant.getRSoutput(row.toString());
					// System.out.println(results);
					String[] fields = results.split(", ");
					if (fields[9].toLowerCase().equals("true")) {
						continue;
					} else {
						genes.add(fields);
					}

				}
				endTime = System.currentTimeMillis();
				totalTime = endTime - startTime;
				System.out.println("nc_Exonic " + totalTime);

				// nc_Splicing
				if (genes.size() != 0) {
					aFunc_refGene = "ncRNA_exonic";
				} else {
					startTime = System.currentTimeMillis();
					select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
							+ UCSCrefGene2.betweenTX_cql(_start, _end) + UCSCrefGene2.isSplice_cql1(_start, _end)
							+ " AND nonCoding = true" + " ALLOW FILTERING;";
					// System.out.println(select);
					rs = session.execute(select);

					for (Row row : rs) {
						String results = Variant.getRSoutput(row.toString());
						// System.out.println(results);
						String[] fields = results.split(", ");
						if (fields[9].toLowerCase().equals("true")) {
							continue;
						} else {
							genes.add(fields);
						}

					}
					select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
							+ UCSCrefGene2.betweenTX_cql(_start, _end) + UCSCrefGene2.isSplice_cql2(_start, _end)
							+ " AND nonCoding = true" + " ALLOW FILTERING;";
					// System.out.println(select);
					rs = session.execute(select);

					for (Row row : rs) {
						String results = Variant.getRSoutput(row.toString());
						// System.out.println(results);
						String[] fields = results.split(", ");
						if (fields[9].toLowerCase().equals("true") || !fields[0].startsWith("NR")) {
							continue;
						} else {
							genes.add(fields);
						}

					}
					endTime = System.currentTimeMillis();
					totalTime = endTime - startTime;
					System.out.println("nc_splicing " + totalTime);

					// UTR5+UTR3+Intronic
					if (genes.size() != 0) {
						aFunc_refGene = "ncRNA_splicing";
					} else {
						startTime = System.currentTimeMillis();
						select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
								+ UCSCrefGene2.betweenTX_cql(_start, _end) + " ALLOW FILTERING;";
						// System.out.println(select);
						rs = session.execute(select);

						for (Row row : rs) {
							String results = Variant.getRSoutput(row.toString());
							// System.out.println(results);
							String[] fields = results.split(", ");
							if (fields[9].toLowerCase().equals("true")) {
								continue;
							} else {
								genes.add(fields);
							}

						}
						endTime = System.currentTimeMillis();
						totalTime = endTime - startTime;
						System.out.println("UTR5+UTR3+Intronic " + totalTime);

						// DownStream+UpStream
						if (genes.size() == 0) {
							startTime = System.currentTimeMillis();
							select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
									+ UCSCrefGene2.isDownStream_cql(_start, _end) + " ALLOW FILTERING;";
							rs = session.execute(select);

							for (Row row : rs) {
								String results = Variant.getRSoutput(row.toString());
								// System.out.println(results);
								String[] fields = results.split(", ");
								if (fields[9].toLowerCase().equals("true")) {
									continue;
								} else {
									genes.add(fields);
								}

							}
							select = "SELECT * FROM refGene2" + " WHERE chrom = " + _chrom
									+ UCSCrefGene2.isUpStream_cql(_start, _end) + " ALLOW FILTERING;";
							rs = session.execute(select);

							for (Row row : rs) {
								String results = Variant.getRSoutput(row.toString());
								// System.out.println(results);
								String[] fields = results.split(", ");
								if (fields[9].toLowerCase().equals("true")) {
									continue;
								} else {
									genes.add(fields);
								}

							}
							endTime = System.currentTimeMillis();
							totalTime = endTime - startTime;
							System.out.println("DownStream+UpStream " + totalTime);
						}
					}
				}
			}
		}

		return genes;

	}

	// Select from chrom = currentChrom
	public static void getGenes4_1_1(int _chrom) {

		refgenes = new ArrayList<String[]>();
		ResultSet rs;

		// long startTime = System.currentTimeMillis();
		String select = "SELECT * FROM refGene" + " WHERE chrom = " + _chrom + " ALLOW FILTERING;";
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				refgenes.add(fields);
			}

		}
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("TolGetGene "+totalTime);

	}

	// Select from *
	public static void getGenes4_1_2() {

		refgenes = new ArrayList<String[]>();
		ResultSet rs;

		// long startTime = System.currentTimeMillis();
		String select = "SELECT * FROM refGene ALLOW FILTERING;";
		rs = session.execute(select);

		for (Row row : rs) {
			String results = Variant.getRSoutput(row.toString());
			// System.out.println(results);
			String[] fields = results.split(", ");
			if (fields[9].toLowerCase().equals("true")) {
				continue;
			} else {
				refgenes.add(fields);
			}

		}
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// System.out.println("TolGetGene "+totalTime);

	}

	public ArrayList<String[]> getGenes4_2(int _chrom, int _start, int _end) {

		ArrayList<String[]> genes = new ArrayList<>();

		for (String[] gene : refgenes) {

			// id(0)|chrom(1)|txstart(2)|txend(2)|cdsend(4)|cdsstart(5)|exoncount(6)|exonends(7)|
			// exonstarts(8)|filter(9)|mrna(10)|mrnaendoffset(11)|mrnastartoffset(12)|name(13)|
			// protein1(14)|strand(15)
			int chrom = Integer.parseInt(gene[1]);
			if (chrom == _chrom) {
				int txStart = Integer.parseInt(gene[2]);
				UCSCrefGene2.setTXstart(txStart);
				int txEnd = Integer.parseInt(gene[3]);
				UCSCrefGene2.setTXend(txEnd);

				if (UCSCrefGene2.betweenTX(_start, _end)) {
					genes.add(gene);
				} else if (UCSCrefGene2.isUpStream(_start, _end)) {

					genes.add(gene);
				} else if (UCSCrefGene2.isDownStream(_start, _end)) {

					genes.add(gene);
				}
			}
		}
		return genes;

	}

	public ArrayList<String> annotateCassandraFromVCF(int _chr, int _start, String _ref, String _alt) {

		String[] id = toAvinput(_start, _ref, _alt);

		String band = cytoband.getCytoBand(_chr, _start);

		ArrayList<String> annotation = new ArrayList<>();

		ResultSet results;

		Statement select = QueryBuilder.select().all().from(connectionParameters[2], table).where(eq("chr", _chr))
				.and(eq("cytoband", band)).and(eq("start", id[0])).and(eq("stop", id[1])).and(eq("ref", id[2]))
				.and(eq("alt", id[3]));
		results = session.execute(select);

		for (Row row : results) {

			annotation.add(row.toString());
		}

		return annotation;
	}

	public ArrayList<String> annotateCassandraFromAVINPUT(int _chr, String _cytoband, int _start, int _end, String _ref,
			String _alt) {

		ArrayList<String> annotation = new ArrayList<>();
		ResultSet results;
		String result = "";

		// Statement select =
		// QueryBuilder.select().all().from(connectionParameters[2], table)
		// .where(eq("chr", _chr))
		// .and(eq("cytoband", _cytoband))
		// .and(eq("start", _start))
		// .and(eq("stop", _end))
		// .and(eq("ref", _ref))
		// .and(eq("alt", _alt));
		String variantID = _chr + ":" + _start + "_" + _end + ":" + _alt.replace(".", "-");
		Statement select = QueryBuilder.select().all().from(connectionParameters[2], "geneStat")
				.where(eq("variantID", variantID));

		results = session.execute(select);

		String row1 = "";
		for (Row row : results) {
			row1 = row.toString();
		}

		// String select2 = "SELECT * FROM geneStat2" + " WHERE chrom = " + _chr
		// + " AND start <= " + _start
		// + " AND end >= " + _end + " ALLOW FILTERING;";
		// System.out.println(select2);
		// results = session.execute(select2);
		//
		// String row2 = "";
		// for (Row row : results) {
		// row2 = row.toString();
		// }
		// result = row1 + row2;
		result = row1;

		annotation.add(result);

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

	public static ArrayList<UCSCrefGene2> getGeneList() {
		return refGenes;

	}

	public void geneStats() {
		int nullProtienCount = 0;

		int rnaCount = 0;

		for (UCSCrefGene2 gene : refGenes) {

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
		for (UCSCrefGene2 gene : refGenes) {
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

	public String getCytoBand2(int _chrom, int _startPos) {

		String bandToReturn = ".";

		if (_chrom >= 1 && _chrom <= 25 && _chrom != 24) {
			ResultSet rs;

			String select = "SELECT * FROM cytoband" + " WHERE chrom = " + _chrom + " AND start <= " + _startPos
					+ " AND stop >= " + _startPos + " ALLOW FILTERING;";
			rs = session.execute(select);

			for (Row row : rs) {
				String results = Variant.getRSoutput(row.toString());
				String[] fields = results.split(", ");
				bandToReturn = fields[2];
			}
		} else if (_chrom == 24 || _chrom == 26) {
			return "mtXY";
		} else if (_chrom == 0) {
			return "0";
		}

		return bandToReturn;
	}

	public final void uploadSingleVariation(int geno, int cov, String subID, String varID) throws IOException {

		String id = subID + ":" + varID;

		// Statement insert = QueryBuilder.insertInto(connectionParameters[2],
		// "variation")
		// .value("id", id).value("subID", subID).value("varID", varID)
		// .value("geno", geno).value("cov", cov).value("cov_alt", null);
		// session.executeAsync(insert);
		String update = "UPDATE variation SET geno = " + geno + " WHERE id = '" + id + "'" + " AND subID = '" + subID
				+ "'" + " AND varID = '" + varID + "'" + " AND cov = " + cov + ";";
		session.executeAsync(update);

	}

	public static void uploadSingleVar(String _annotation) throws IOException {

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

		String[] fields = _annotation.trim().split("\t");
		String variantID = fields[0] + ":" + fields[2] + "_" + fields[3] + ":" + fields[5];

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

	private String getHGVStype(String _ref, String _alt) {

		String type = "";

		if (_ref.replace(".", "").length() != _alt.replace(".", "").length()) {

			char[] refArray = _ref.toCharArray();
			char[] altArray = _alt.toCharArray();

			String newRef = "";
			String newAlt = "";

			int numMatchChar = 0;

			if (_ref.length() > _alt.length()) {

				for (int i = 0; i < altArray.length; i++) {
					if (refArray[i] == altArray[i]) {
						numMatchChar++;
					} else {
						break;
					}
				}

			} else {
				for (int i = 0; i < refArray.length; i++) {
					if (refArray[i] == altArray[i]) {
						numMatchChar++;
					} else {
						break;
					}
				}
			}
			if (numMatchChar > 0) {
				if (numMatchChar == altArray.length) {
					newAlt = ".";
				} else {
					for (int i = numMatchChar; i < altArray.length; i++) {
						newAlt += altArray[i];
					}
				}
				if (numMatchChar == refArray.length) {
					newRef = ".";
				} else {
					for (int i = numMatchChar; i < refArray.length; i++) {
						newRef += refArray[i];
					}
				}
			} else {
				newRef = _ref;
				newAlt = _alt;
			}

			if (!newRef.equals(".") && newAlt.equals(".")) {
				type = "del";
			} else if (newRef.equals(".") && !newAlt.equals(".")) {
				type = "ins"; // or dup or con
			} else {
				type = "delins";
			}
		} else if (_alt.length() == 1) {
			type = "sub";
		} else if (Annotation.flipStrand(_alt).equals(_ref)) {
			type = "inv";
		} else {
			type = "delins";
		}

		return type;

	}
}
