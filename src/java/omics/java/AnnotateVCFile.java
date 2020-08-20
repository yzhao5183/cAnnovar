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
public class AnnotateVCFile {

	/**
	 * @param args
	 *            the command line arguments
	 * @throws java.io.IOException
	 */

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

		String keyspace = "annovar";
		String[] connectionParameters = { "localhost", "9042", keyspace };

		String table = "geneStat";
		String cytobandFile = "/home/yqzhao/workspace/AnnotateVCF/humandb/hg19_cytoBand.txt";
		String refGene = "/home/yqzhao/workspace/AnnotateVCF/humandb/hg19_refGene.txt";
		String refGeneMrna = "/home/yqzhao/workspace/AnnotateVCF/humandb/hg19_refGeneMrna.fa";
		String testVCF1 = "/home/yqzhao/workspace/AnnotateVCF/data/LID57247.vcf";

		//boolean upload = true;
		boolean upload = false;

		if (upload) {
			// Annotation
			Annotation an = new Annotation(connectionParameters, "", "");
			// PrintWriter writer = new PrintWriter("/home/yqzhao/workspace/AnnotateVCF/data/log.txt", "UTF-8");

			// an.uploadFreqtoCassandra("esp", writer); //NHLBI funded exome sequencing project
			// an.uploadFreqtoCassandra("ExAC", writer); //1h //Exome Aggregation Consortium, aggregate and harmonize exome sequencing data from a wide variety of large-scale sequencing projects
			// an.uploadFreqtoCassandra("ALL", writer); //8h //1000 Genomes Project (2015 Aug) alternative allele frequency
			// an.uploadFreqtoCassandra("AFR", writer); //4h
			// an.uploadFreqtoCassandra("AMR", writer); //3h
			// an.uploadFreqtoCassandra("EAS", writer); //2h
			// an.uploadFreqtoCassandra("EUR", writer); //2h
			// an.uploadFreqtoCassandra("SAS", writer); //2h
			
			// an.uploadFreqtoCassandra("gerpgt2", writer); //100h //typically regarded as evolutioanrily conserved and potentially functional. Anything less than 2 is not informative, which helps reduce file size substantially.
			// an.uploadFreqtoCassandra("cg69", writer);	//2h //allele frequency data from 69 related subjects (including the 46 unrelated subjects). We can set up a MAF threshold of 0.05, so that only very common SNPs are dropped. 
			// an.uploadFreqtoCassandra("clinvar", writer); //relationships among variation and human health
			// an.uploadFreqtoCassandra("cosmic70", writer); //"Catalogue Of Somatic Mutations In Cancer". It includes somatic mutations reported in literature in various types of cancers
			// an.uploadFreqtoCassandra("nci60", writer); //allele frequency information from these 60 cell lines based on their exome sequencing data
			// an.uploadFreqtoCassandra("fathmm", writer); //*1000h //Functional Analysis through Hidden Markov Models 
			// an.uploadFreqtoCassandra("gwava", writer); //12h //GWAVA is a tool which aims to predict the functional impact of non-coding genetic variants based on a wide range of annotations of non-coding elements (largely from ENCODE/GENCODE), along with genome-wide properties such as evolutionary conservation and GC-content.
			// an.uploadFreqtoCassandra("eigen", writer); //*1000h //unsupervised learning approach, integrating functional genomic annotations for coding and noncoding variants
			// an.uploadFreqtoCassandra("dbnsfp30a", writer); //8h
			// an.uploadFreqtoCassandra("kaviar", writer); //23h //Kaviar (~Known VARiants) is a compilation of SNVs, indels, and complex variants observed in humans, designed to facilitate testing for the novelty and frequency of observed variants. 

			// an.uploadFreqtoCassandra("snp138", writer); //6h //variant reported in dbSNP
			// an.uploadFreqtoCassandra("gwasCatalog", writer);
			 
			// an.uploadFreqtoCassandra("phastConsElements46way", writer);
			// an.uploadFreqtoCassandra("tfbsConsSites", writer);
			// an.uploadFreqtoCassandra("wgRna", writer);
			// an.uploadFreqtoCassandra("targetScanS", writer);
			// an.uploadFreqtoCassandra("genomicSuperDups", writer);
			// an.uploadFreqtoCassandra("dgvMerged", writer);

			// writer.close();

			// an.uploadCytoband(cytobandFile);
			// an.uploadRefGene(refGene);
			// an.uploadRefGene2(refGene);
			// an.initCytoband(cytobandFile);
			// an.uploadRefGeneMrna(refGene, refGeneMrna);
			// an.filterCassandraRefGene(refGene, refGeneMrna);

			 an.uploadAnnotatedVar(testVCF1 + ".annotated.txt1");
			// an.uploadVariation(testVCF1);

			// Annotation an = new Annotation();
			//
			// an.initCytoband(cytobandFile);
			// an.initRefGene(refGene, refGeneMrna);
			// an.geneStats();
			// //remove ref gene startsWith("NR")
			// an.filterTransctipts();
			// an.geneStats();

			// String annotatedVCFout1 =
			// "/home/yqzhao/workspace/AnnotateVCF/data/84060.vcf.annotated.txt";
			// annotateVCF(testVCF1, an, annotatedVCFout1);

			an.closeConnection();
		}

		//boolean test = true;
		boolean test = false;

		if (!upload & test) {
			// Annotation2 - from Cassandra
			Annotation2 an2 = new Annotation2(connectionParameters, "", "");
			// String annotatedVCFout2 =
			// "/home/yqzhao/workspace/AnnotateVCF/data/84060.vcf.annotated2.txt";
			// annotateVCF2(testVCF1, an2, annotatedVCFout2);
			// String newTests1 = "1\t1p31.1\t75072264\t75072264\tG\tT";

			// String newTests1 = "13\t.\t32913729\t32913730\tC\tCT";
			String newTests1 = "6\t.\t7542148\t7542148\t.\tA";
			long startTime = System.currentTimeMillis();
			System.out.println(an2.annotateVariant2_1(newTests1));
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Tol " + totalTime);
			an2.closeConnection();
			// an2.initCytoband(cytobandFile);
			// an2.initRefGene(refGene, refGeneMrna);
			// an2.filterTransctipts();

		}

		if (!upload & !test) {
			// Annotation2 - from Cassandra
			Annotation2 an2 = new Annotation2(connectionParameters, "", "");
			String annotatedVCFout2 = testVCF1 + ".annotated.txt";
			long startTime = System.currentTimeMillis();
			annotateVCF2(testVCF1, an2, annotatedVCFout2);
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Tol " + totalTime);
			// String newTests1 = "1\t1p31.1\t75072264\t75072264\tG\tT";
			// String newTests1 = "13\t.\t32913729\t32913730\tC\tCT";
			// System.out.println(an2.annotateVariant2(newTests1));
			an2.closeConnection();
			// an2.initCytoband(cytobandFile);
			// an2.initRefGene(refGene, refGeneMrna);
			// an2.filterTransctipts();

		}

		// Annotation3
		// Annotation3 an3 = new Annotation3(connectionParameters, "", "");
		// an3.initCytoband(cytobandFile);
		// an3.initRefGene(refGene, refGeneMrna);
		// an3.filterTransctipts();
		// String annotatedVCFout3 =
		// "/home/yqzhao/workspace/AnnotateVCF/data/LID57247.vcf.annotated3.txt";
		// annotateVCF3(testVCF1, an3, annotatedVCFout3);
		// an3.closeConnection();

		// Test queries
		// String newTests1 = "1\t1p31.1\t75072264\t75072264\tG\tT";
		// System.out.println(an.annotateVariant(newTests1));
		// String newTests2 = "2\t2p13.1\t74185876\t74185876\tA\tT";
		// System.out.println(an.annotateVariant(newTests2));
		// String newTests3 = "23\tq22.2\t103268241\t103268241\tG\tA";
		// System.out.println(an.annotateVariant(newTests3));
		// String testVariant_nonframeshiftInsertion =
		// "1\tp36.33\t1900106\t1900106\t.\tCTC";
		// String testVariant_nonframeshiftDeletion =
		// "1\tq44\t247978542\t247978544\tAGG\t.";
		// String testVariant_nonframeshiftSubstitution =
		// "1\tp36.13\t16375063\t16375064\tCA\tGC";
		// String testVariant_nonsynonymousSubstitution =
		// "1\tp36.13\t16375063\t16375064\tTG\tGC";
		// String testVariant_frameshiftinsertion =
		// "1\tp32.3\t54605319\t54605319\t.\tC";
		// String testVariant_frameshiftDeletion =
		// "1\tq21.1\t144873963\t144873963\tT\t.";
		// String testVariant_nonsynonymousSNV =
		// "2\tp25.1\t7137067\t7137067\tA\tG";
		// String testVariant_nonsynonymousSNV2 =
		// "1\tp36.33\t69511\t69511\tA\tG";
		// String testVariant_synonymousSNV2 = "1\tp36.33\t69552\t69552\tG\tC";
		// String testVariant_nonsynonymousSNV3 =
		// "1\tp36.33\t69569\t69569\tT\tC";
		// String testVariant_synonymousSNV =
		// "2\tp25.1\t7154632\t7154632\tT\tC";
		// String testVariant_UTR3 = "2\tp25.1\t7181486\t7181486\tA\tG";
		// String testVariant_UTR5 = "2\tp25.1\t8822266\t8822266\tC\tT";
		// String testVariant_splicing = "2\tp21\t42165754\t42165754\tC\tT";
		// String testVariant_intronic = "1\tp36.33\t1900339\t1900339\tA\tG";
		// String testVariant_intergenic = "2\tp25.1\t8825997\t8825997\tC\tT";
		// System.out.println(an.getVariantHeaders());
		// System.out.println(an.annotateVariant(testVariant_nonframeshiftInsertion));
		// System.out.println(an.annotateVariant(testVariant_nonframeshiftDeletion));
		// System.out.println(an.annotateVariant(testVariant_nonframeshiftSubstitution));
		// System.out.println(an.annotateVariant(testVariant_nonsynonymousSubstitution));
		// System.out.println(an.annotateVariant(testVariant_frameshiftinsertion));
		// System.out.println(an.annotateVariant(testVariant_frameshiftDeletion));
		// System.out.println(an.annotateVariant(testVariant_nonsynonymousSNV));
		// System.out.println(an.annotateVariant(testVariant_nonsynonymousSNV2));
		// System.out.println(an.annotateVariant(testVariant_synonymousSNV2));
		// System.out.println(an.annotateVariant(testVariant_nonsynonymousSNV3));
		// System.out.println(an.annotateVariant(testVariant_synonymousSNV));
		// System.out.println(an.annotateVariant(testVariant_UTR3));
		// System.out.println(an.annotateVariant(testVariant_UTR5));
		// System.out.println(an.annotateVariant(testVariant_splicing));
		// System.out.println(an.annotateVariant(testVariant_intronic));
		// System.out.println(an.annotateVariant(testVariant_intergenic));

		// String testVCF1 =
		// "/home/yqzhao/workspace/AnnotateVCF/data/84060.vcf";
		// String annotatedVCFout1 =
		// "/home/yqzhao/workspace/AnnotateVCF/data/84060.vcf.annotated.txt";
		// annotateVCF(testVCF1, an, annotatedVCFout1);

		// String testVCF2 =
		// "/home/hadoop/Workspace/annovar/vcfBeta-GS000035897-ASM.upQual.filter.dp.vcf";
		// String annotatedVCFout2 =
		// "/home/hadoop/Workspace/annovar/vcfBeta-GS000035897-ASM.upQual.filter.dp.annotated.txt";
		// annotateVCF(testVCF2, an, annotatedVCFout2);
		//
		// String testVCF3 =
		// "/home/hadoop/Workspace/annovar/vcfBeta-GS000035898-ASM.upQual.filter.dp.vcf";
		// String annotatedVCFout3 =
		// "/home/hadoop/Workspace/annovar/vcfBeta-GS000035898-ASM.upQual.filter.dp.annotated.txt";
		// annotateVCF(testVCF3, an, annotatedVCFout3);

		// printArrayListFile(avinput,
		// "/home/hadoop/Workspace/avinput/anOut.txt");
		//
		// ArrayList<String> variant1 =
		// an.annotateVariant(Integer.parseInt(testVariant1[0]),testVariant1[1],Integer.parseInt(testVariant1[2]),Integer.parseInt(testVariant1[3]),testVariant1[4],testVariant1[5]);
		//
		// ArrayList<String> variant2 =
		// an.annotateVariant(Integer.parseInt(testVariant2[0]),testVariant2[1],Integer.parseInt(testVariant2[2]),Integer.parseInt(testVariant2[3]),testVariant2[4],testVariant2[5]);
		//
		// System.out.println("variant1");
		//
		// printArrayList(variant1);
		//
		// System.out.println("variant2");
		//
		// printArrayList(variant2);
		//
		System.exit(0);

	}

	public static ArrayList<String> getVCFHeader(String _arg) throws FileNotFoundException, IOException {

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

	public static ArrayList<String> parseVCFtoAvinput(String _arg, Annotation _an)
			throws FileNotFoundException, IOException {

		ArrayList<String> avinput = new ArrayList<>();

		try (BufferedReader brHeader = new BufferedReader(new FileReader(_arg))) {
			String line;

			while ((line = brHeader.readLine()) != null) {

				if (!line.startsWith("#")) {

					// System.out.println(line);
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

						String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
								fields[4].trim());

						avinput.add((chrom + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
								+ startStopRefAlt[2] + "\t" + startStopRefAlt[3] + "\t" + genotype + "\t" + QUAL + "\t"
								+ subject[DP]));

					} else if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.,]+$")) {
						String[] altAlt = fields[4].trim().split(",");
						for (String alt : altAlt) {
							// System.out.println(line);
							int chrom = Cytoband.getIntChrom(fields[0]);

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									alt);

							avinput.add((chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0]))
									+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t" + startStopRefAlt[2]
									+ "\t" + alt + "\t" + genotype + "\t" + QUAL + "\t" + subject[DP]));

						}
					}

				}

			}
		}

		return avinput;

	}

	public static void annotateVCF(String _inputVCF, Annotation _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];

							writer.println(_an.annotateVariant(variant));

							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	// Select from cassandra||betweenTx&Upstream&Downstream
	public static void annotateVCF2(String _inputVCF, Annotation2 _an, String _outputFile)
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
				
						int geno = -1; int cov = -1;
				
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
						if(GT!=-1){
							if(sample[GT].contains("/")){
								String[] genos = sample[GT].split("/");
								geno = Integer.parseInt(genos[0])+Integer.parseInt(genos[1]); 
								if(geno > 2){
									geno = 2;
								}
							}
						}


						if(DP!=-1){
							cov = Integer.parseInt(sample[DP]);
						}

						if (fields[3].trim().matches("^[AGCT\\.]+$") && fields[4].trim().matches("^[AGCT\\.]+$")) {

							int chrom = Cytoband.getIntChrom(fields[0]);

							if (currentChrom != chrom) {
								System.out.println("Processing Chromosome " + chrom);
								currentChrom = chrom;
							}

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(), fields[4].trim());
							String variant = "";
							String variantID = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
								variantID = chrom + ":" + startStopRefAlt[0] + "_" + startStopRefAlt[1] + ":"
										+ startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
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

	// Select from betweenTx&Upstream&Downstream
	public static void annotateVCF2_1(String _inputVCF, Annotation2 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							// long startTime = System.currentTimeMillis();
							writer.println(_an.annotateVariant2_1(variant));
							// long endTime = System.currentTimeMillis();
							// long totalTime = endTime - startTime;
							// System.out.println("Tol "+totalTime);
							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	// Select from betweenTx||Upstream||Downstream
	public static void annotateVCF2_2(String _inputVCF, Annotation2 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							// long startTime = System.currentTimeMillis();
							writer.println(_an.annotateVariant2_2(variant));
							// long endTime = System.currentTimeMillis();
							// long totalTime = endTime - startTime;
							// System.out.println("Tol "+totalTime);
							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	// Select from Exon||Splicing||UTR3/UTR5/Intron||UP/Downstream
	public static void annotateVCF2_3(String _inputVCF, Annotation2 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							// long startTime = System.currentTimeMillis();
							writer.println(_an.annotateVariant3(variant));
							// long endTime = System.currentTimeMillis();
							// long totalTime = endTime - startTime;
							// System.out.println("Tol "+totalTime);
							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	// Select from chrom = currentChrom
	public static void annotateVCF2_4_1(String _inputVCF, Annotation2 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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
								Annotation2.getGenes4_1_1(chrom);
							}

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							// long startTime = System.currentTimeMillis();
							writer.println(_an.annotateVariant4(variant));
							// long endTime = System.currentTimeMillis();
							// long totalTime = endTime - startTime;
							// System.out.println("Tol " + totalTime);
							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	// Select from *
	public static void annotateVCF2_4_2(String _inputVCF, Annotation2 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);
		Annotation2.getGenes4_1_2();

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand2(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							// long startTime = System.currentTimeMillis();
							writer.println(_an.annotateVariant4(variant));
							// long endTime = System.currentTimeMillis();
							// long totalTime = endTime - startTime;
							// System.out.println("Tol " + totalTime);
							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

	public static void annotateVCF3(String _inputVCF, Annotation3 _an, String _outputFile)
			throws FileNotFoundException, IOException, InterruptedException, ExecutionException {
		// try (PrintWriter writer = new PrintWriter(_file, "UTF-8")) {
		// for (String str : _list) {
		// writer.println(str);
		//
		// writer.flush();
		// }
		// writer.close();
		// }

		System.out.println("Annotating vcf " + _inputVCF);

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

							String[] startStopRefAlt = _an.toAvinput(Integer.parseInt(fields[1]), fields[3].trim(),
									fields[4].trim());
							String variant = "";
							if (chrom == 0) {
								// e.g. chr1_gl000191_random
								variant = chrom + "\t" + "0" + "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1]
										+ "\t" + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
							} else // chr1-23+X+Y+M+MT
								variant = chrom + "\t" + _an.getCytoBand(chrom, Integer.parseInt(startStopRefAlt[0]))
										+ "\t" + startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
										+ startStopRefAlt[2] + "\t" + startStopRefAlt[3];

							writer.println(_an.annotateVariant(variant));

							// avinput.add((chrom + "\t" + startStopRefAlt[0] +
							// "\t" + startStopRefAlt[1] + "\t" +
							// startStopRefAlt[2] + "\t" + startStopRefAlt[3] +
							// "\t" + genotype + "\t" + QUAL + "\t" +
							// subject[DP] ));
						}
						// else if (fields[3].trim().matches("^[AGCT\\.]+$") &&
						// fields[4].trim().matches("^[AGCT\\.,]+$")) {
						// String[] altAlt = fields[4].trim().split(",");
						// for (String alt : altAlt) {
						// //System.out.println(line);
						// int chrom = Cytoband.getIntChrom(fields[0]);
						//
						// if (currentChrom != chrom) {
						// System.out.println("Processing Chromosome " +
						// currentChrom);
						// currentChrom = chrom;
						// }
						//
						// String[] startStopRefAlt =
						// _an.toAvinput(Integer.parseInt(fields[1]),
						// fields[3].trim(), alt);
						//
						// String variant = chrom + "\t" +
						// _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0])) + "\t" +
						// startStopRefAlt[0] + "\t" + startStopRefAlt[1] + "\t"
						// + startStopRefAlt[2] + "\t" + startStopRefAlt[3];
						// writer.println(_an.annotateVariant(variant));
						//
						// //avinput.add((chrom + "\t" + _an.getCytoBand(chrom,
						// Integer.parseInt(startStopRefAlt[0]))+ "\t"
						// +startStopRefAlt[0] + "\t" + startStopRefAlt[1] +
						// "\t" + startStopRefAlt[2] + "\t" + alt+ "\t" +
						// genotype + "\t" + QUAL + "\t" + subject[DP]));
						// }
						// }

					}

					writer.flush();

				}
			}

		}

	}

}
