/**
 * Copyright (c) 2001-2008 U-Mart Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * ---------------------------------------------------------------------
 */
package experiments;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import analysis.ParetoRanking;

import serverSA.UMartStandAloneCUI;

/**
 * 第８章における実験２を行うクラスです．設定ファイルの作成→実験→パレートランキングに
 * よる結果集計，までを一括して行います．
 */
public class Experiment2 {

	/**
	 * 引数で与えられた回数の実験を行い．結果をパレートランキング法で集計します．
	 * 引数を与えない場合には10回分の実験を行います．
	 * @param args　実験回数
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		int numOfExp = 10;
		if (args.length == 1) {
			numOfExp = Integer.parseInt(args[0]);
		}
		
		String dir = "experiments/";
		final String exp_type = "exp2";
		String logDir = dir + exp_type;
	
		CSVCreator creator = new CSVCreator();
		creator.initialize();
		creator.createExp2Files(numOfExp);
			
		File file = new File (logDir);
		File[] list = file.listFiles();
	
		if (list.length == 0) {
			System.err.println("CSV files are not prepared.");
		}
		
		for (int i = 0; i < list.length; i++) {
			boolean isSimpleLog = true;
			String fileName = list[i].getName();
			String[] strs = fileName.split("_");		
			String baseDir = list[i].toString();
			int randomSeed = Integer.parseInt(strs[strs.length - 1]);
			try {
				UMartStandAloneCUI cui = new UMartStandAloneCUI(baseDir, isSimpleLog, randomSeed);
				cui.doLoop();
				System.out.println(fileName + ": done");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(5);
			}
			
		}
		
		String[] dirList = new String[list.length];
		for (int i = 0; i < dirList.length; i++) {
			dirList[i] = new String(list[i].toString());
			// System.out.println("dirList[" + i + "] = " + dirList[i]);
		}		
		ParetoRanking pr = new ParetoRanking(dirList);
        pr.calcParetoRank();
        //pr.printResult();
        PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(dir + "Rankiing_" + exp_type + ".csv")));
        pr.printResult(pw);
        pw.close();
        System.out.println("Ranking is calculated.");
        System.out.println("Experiment 2 is done.");
	}
}