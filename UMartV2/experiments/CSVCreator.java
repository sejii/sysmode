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

import java.io.*;
import java.util.*;

/**
 * 第８章での加速実験に使われる設定ファイル（CSV形式）を作成するプログラムです．
 * このクラスは，実験用クラス(Experiment1, Experiment2, Experiment3)
 * によって使用されるため，ユーザにより明示的に実行される必要はありません．
 */

public class CSVCreator {
	/**
	 * 標準エージェントを保持するリスト
	 */
	List standardAgents;
	/**
	 * 作成したエージェントを保持するリスト
	 */
	List myAgents;
	/** 
	 * 実験開始時点
	 */
	Map startingPoints;
	/**
	 * 最大日数
	 */
	int maxDate;
	/**
	 * １日あたりのセッション数
	 */
	int numOfSessionsPerDay;
	/**
	 * 実験用ディレクトリ（フォルダ）
	 */
	String dirName = "experiments/";
	/**
	 * 標準エージェントが記述されているファイル名
	 */
	String stdAgentsFile = "StandardAgents.txt";
	/**
	 * 作成したエージェントを記述したファイル名
	 */
	String myAgentsFile = "MyAgents.txt";
	/**
	 * 価格系列ファイル名
	 */
	String priceDefinitionFile = "Prices.txt";
	/**
	 * 乱数
	 */
	Random rand = new Random();
	/**
	 * ログイン名
	 */
	public static final String LOGIN_NAME = "LOGIN_NAME";
	/**
	 * クラス名
	 */
	public static final String CLASS_NAME = "CLASS_NAME";
	/**
	 * 現物価格ファイル名
	 */
	public static final String SPOT_FILENAME = "SPOT_FILENAME";
	/**
	 * 実験開始時点
	 */
	public static final String STARTING_POINT = "STARTING_POINT";

	/**
	 * リストやハッシュマップの初期化を行います．
	 *
	 */
	public CSVCreator() {
		standardAgents = new ArrayList();
		myAgents = new ArrayList();
		startingPoints = new HashMap();
	}

	/**
	 * 設定ファイルを作成します．
	 * @param dir リソースファイルのあるディレクトリ(フォルダ）
	 * @param members　実験に参加するエージェント
	 * @param spotFilename　現物ファイル名
	 * @param startingPoint　取引開始時点
	 */
	private void createFiles(String dir, List members, String spotFilename,
			int startingPoint) {
		String resourceDirName = dir + "resources/";
		File resourceDir = new File(resourceDirName);
		resourceDir.mkdirs();
		createMembers(resourceDirName, members);
		createTimeSeriesDefinitions(resourceDirName, spotFilename,
				startingPoint);
		createSpotFile(resourceDirName, spotFilename);
	}

	/**
	 * 実験用エージェント定義ファイル：Members.csv を作成します．
	 * @param dir　作成するディレクトリ(フォルダ）
	 * @param members　実験に用いるエージェントのリスト
	 */
	private void createMembers(String dir, List members) {
		File outputFile = new File(dir + "/Members.csv");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
			pw.println("LoginName,Password,Attribute,Connection,Access(Allow_All)(CHART:BOARD:ORDER:ORDER_RESULT:PROFIT:POSITION:STDOUT),RealName,SystemParameters,Seed,InitialCash,TradingUnit,FeePerUnit,MarginRate,MaxLoan,Interest");
			Iterator i = members.iterator();
			int counter = 1;
			while (i.hasNext()) {
				HashMap info = (HashMap) i.next();
				String loginName = (String) info.get(CSVCreator.LOGIN_NAME);
				String className = (String) info.get(CSVCreator.CLASS_NAME);
				String str = loginName + ",passwd" + counter
						+ ",Machine,Local,," + className + ",,"
						+ Math.abs(rand.nextInt())
						+ ",1000000000,1000,0,300000,30000000,0.1";
				pw.println(str);
				counter++;
			}
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 実験のための価格系列定義ファイルを作成します．
	 * @param dir　ファイルを作成するディレクトリ（フォルダ）
	 * @param spotFilename　現物価格ファイル
	 * @param startingPoint　取引開始時点
	 */
	private void createTimeSeriesDefinitions(String dir, String spotFilename,
			int startingPoint) {
		File outputFile = new File(dir + "/TimeSeriesDefinitions.csv");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(outputFile));
			pw
					.println("Nickname,Filename,BrandName,StartStep,MaxDate,NoOfSessionsPerDay");
			pw
					.println("Default,resource:" + spotFilename + ",J30,"
							+ startingPoint + "," + maxDate + ","
							+ numOfSessionsPerDay);
			pw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 引数で指定されたディレクトリ(フォルダ）に，同じく引数で指定されたファイル名で，
	 * 現物価格を保持するファイルを作成します．
	 * @param dir ファイルを作成するディレクトリ（フォルダ）
	 * @param filename　ファイル名
	 */
	private void createSpotFile(String dir, String filename) {
		File inputFile = new File(dirName + filename);
		File outputFile = new File(dir + "/" + filename);
		FileReader fr;
		FileWriter fw;
		String str = null;
		try {
			fr = new FileReader(inputFile);
			BufferedReader br = new BufferedReader(fr);
			fw = new FileWriter(outputFile);
			PrintWriter pw = new PrintWriter(fw);
			while ((str = br.readLine()) != null) {
				pw.println(str);
			}
			br.close();
			pw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 引数で与えられた回数分の実験を行えるような，実験１用の設定ファイルを作成します．
	 * @param numOfExp1　実験回数
	 */
	public void createExp1Files(int numOfExp1) {
		Iterator i = myAgents.iterator();
		while (i.hasNext()) {
			List list = new ArrayList();
			HashMap info = (HashMap) i.next();
			list.add(info);
			Iterator k = standardAgents.iterator();
			while (k.hasNext()) {
				list.add(k.next());
			}
			// printList(list);
			String name = (String) info.get(CSVCreator.LOGIN_NAME);
			String dir = dirName + "exp1/" + name;
			Iterator j = startingPoints.keySet().iterator();
			while (j.hasNext()) {
				String situation = (String) j.next();
				HashMap spotInfo = (HashMap) startingPoints.get(situation);
				String spotFilename = (String) spotInfo.get(SPOT_FILENAME);
				int startingPoint = ((Integer) spotInfo.get(STARTING_POINT))
						.intValue();
				for (int expNo = 1; expNo <= numOfExp1; ++expNo) {
					String subDirName = dir + "_" + situation + "_" + expNo
							+ "/";
					//File directory = new File(subDirName);
					createFiles(subDirName, list, spotFilename, startingPoint);
				}
			}
		}
		System.out.println("CSV files for Exp. 1: created");
	}

	/**
	 * 引数で与えられた回数分の実験を行えるような，実験２用の設定ファイルを作成します．
	 * @param numOfExp2　実験回数
	 */
	public void createExp2Files(int numOfExp2) {
		Iterator i = myAgents.iterator();
		List list = new ArrayList();
		while (i.hasNext()) {
			list.add(i.next());
		}
		i = standardAgents.iterator();
		while (i.hasNext()) {
			list.add(i.next());
		}
		Iterator j = startingPoints.keySet().iterator();
		while (j.hasNext()) {
			String situation = (String) j.next();
			HashMap spotInfo = (HashMap) startingPoints.get(situation);
			String spotFilename = (String) spotInfo.get(SPOT_FILENAME);
			int startingPoint = ((Integer) spotInfo.get(STARTING_POINT))
					.intValue();
			for (int expNo = 1; expNo <= numOfExp2; ++expNo) {
				String subDirName = dirName + "exp2/" + situation
						+ "_" + expNo + "/";
				createFiles(subDirName, list, spotFilename, startingPoint);
			}
		}
		System.out.println("CSV files for Exp. 2: created");
	}

	/**
	 * 引数で与えられた回数分の実験を行えるような，実験３用の設定ファイルを作成します．
	 * @param numOfExp3　実験回数
	 */
	public void createExp3Files(int numOfExp3) {
		for (int i = 1; i <= numOfExp3; i++) {
			List list = new ArrayList();
			Iterator itr = myAgents.iterator();
			while (itr.hasNext()) {
				list.add(itr.next());
			}
			itr = standardAgents.iterator();
			while (itr.hasNext()) {
				list.add(itr.next());
			}
			dropHalf(list);
			Iterator j = startingPoints.keySet().iterator();
			while (j.hasNext()) {
				String situation = (String) j.next();
				String subDirName = dirName + "exp3/set" + i + "_"
						+ situation + "_1/";
				HashMap spotInfo = (HashMap) startingPoints.get(situation);
				String spotFilename = (String) spotInfo.get(SPOT_FILENAME);
				int startingPoint = ((Integer) spotInfo.get(STARTING_POINT))
						.intValue();
				createFiles(subDirName, list, spotFilename, startingPoint);
			}
		}
		System.out.println("CSV files for Exp. 3: created");
	}

	/**
	 * 引数で与えられたリストから要素の半分をランダムに削除します．
	 * @param list
	 */
	private void dropHalf(List list) {
		int halfSize = list.size() / 2;
		for (int i = 0; i < halfSize; i++) {
			list.remove(rand.nextInt(list.size()));
		}
	}

	/**
	 * 初期化：標準エージェント，作成したエージェント，および取引開始時点を設定します．
	 *
	 */
	public void initialize() {
		setAgents(stdAgentsFile, standardAgents);
		setAgents(myAgentsFile, myAgents);
		setStartingPoints(priceDefinitionFile);
	}

	/**
	 * 取引開始時点の設定を行います．
	 * @param filename ファイル名
	 */
	private void setStartingPoints(String filename) {
		File file = new File(dirName + filename);
		FileReader fr = null;
		String str = null;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((str = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(str);
				if (str.startsWith("#")) {
					continue;
				} else if (str.startsWith("maxDate")) {
					st.nextToken();
					maxDate = Integer.parseInt(st.nextToken());
					continue;
				} else if (str.startsWith("numOfSessionsPerDay")) {
					st.nextToken();
					numOfSessionsPerDay = Integer.parseInt(st.nextToken());
					continue;
				}
				String situation = st.nextToken();
				String spotFilename = st.nextToken();
				// System.out.println("spotFilename: " + spotFilename);
				int startingPoint = Integer.parseInt(st.nextToken());
				HashMap info = new HashMap();
				info.put(CSVCreator.SPOT_FILENAME, spotFilename);
				info.put(CSVCreator.STARTING_POINT, new Integer(startingPoint));
				startingPoints.put(situation, info);
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 引数でしていしたファイル名に，同じく引数でしていしたエージェント集合を書き込みます．
	 * @param filename
	 * @param agentList
	 */
	private void setAgents(String filename, List agentList) {
		File file = new File(dirName + filename);
		FileReader fr = null;
		String str = null;
		try {
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			while ((str = br.readLine()) != null) {
				if (str.startsWith("#") || str.equals("")) {
					continue;
				}
				StringTokenizer st = new StringTokenizer(str);
				HashMap info = new HashMap();
				info.put(CSVCreator.LOGIN_NAME, st.nextToken());
				info.put(CSVCreator.CLASS_NAME, st.nextToken());
				agentList.add(info);
			}
			fr.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 引数で与えられたリストの内容を表示します．
	 * @param list
	 */
	private void printList(List list) {
		Iterator i = list.iterator();
		while (i.hasNext()) {
			System.out.println(i.next());
		}
	}

	/** 
	 * 引数で与えられたハッシュ表の内容を表示します．
	 * @param map
	 */
	private void printMap(Map map) {
		Iterator i = startingPoints.keySet().iterator();
		while (i.hasNext()) {
			Object obj = i.next();
			System.out.println(obj + ", " + startingPoints.get(obj));
		}
	}

	/**
	 * 各種変数の表示：最大日数，１日あたりのセッション数，標準エージェント，
	 * 作成したエージェント，開始時点，を表示します．
	 *
	 */
	private void PrintAll() {
		System.out.println("************Parameters***************");
		System.out.println("maxDate = " + maxDate);
		System.out.println("numOfSessionsPerDay = " + numOfSessionsPerDay);
		System.out.println("************Standards***************");
		printList(standardAgents);
		System.out.println("************MyAgents***************");
		printList(myAgents);
		System.out.println("************StartingPoints***************");
		printMap(startingPoints);
	}

}
