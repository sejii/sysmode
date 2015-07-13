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
package analysis;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * パレートランキングを求めるためのクラスです．
 * 現在は, 最大，平均，勝率，破産率の４つに決めうちになっています．
 * 新しい指標が出てくれば，対応する可能性もあります．
 */
public class ParetoRanking {
	
	/** 指標に対してランクを作るクラスの配列 */
	private AbstractRankMaker[] rankMaker_;

	/** パレートランクの配列 */
	private ArrayList paretoRankingList_ = new ArrayList();

	/** 指標に対してランクを作るクラスの数 */
	private int maxRankMaker_ = 4;

	/** シミュレーション数 */
	private int simulationNum_ = 0;

	/** エージェント数 */
	private int totalAgentNum_ = 0;

	/**
	 * コンストラクタです．
	 * @param dirs 実験ディレクトリ
	 */
	public ParetoRanking(String[] dirs) {
		simulationNum_ = dirs.length;
		rankMaker_ = new AbstractRankMaker[maxRankMaker_];
		// 最大
		rankMaker_[0] = new MaxProfitRankMaker();
		// 平均
		rankMaker_[1] = new MeanProfitRankMaker();
		// 勝率
		rankMaker_[2] = new WinningAverageRankMaker();
		// 破産率
		rankMaker_[3] = new BankruptcyRateRankMaker();
		SettlementReader sr = new SettlementReader();
		// dirs 以下のディレクトリにある Settlement_account.csv を読み込む．
		ArrayList list = sr.getAllSettlementHashList(dirs);
		for (int i = 0; i < rankMaker_.length; i++) {
			rankMaker_[i].setAllResult(list);
		}
		// ログイン名を利用して，名前と空の RankArray を持つ NameAndRankArray の ArrayList を作る．
		ArrayList allName = rankMaker_[0].getAllLoginName();
		totalAgentNum_ = allName.size();
		Iterator iter = allName.iterator();
		while (iter.hasNext()) {
			String name = (String) iter.next();
			//          RankArray は要素数 maxRankMaker_ 個のベクトル．
			NameAndRankArray nr = new NameAndRankArray(name, new RankArray(
					maxRankMaker_));
			paretoRankingList_.add(nr);
		}

		for (int i = 0; i < rankMaker_.length; i++) {
			HashMap tmpMap = rankMaker_[i].getRank();
			// tmpMap の key が全ログイン名．
			for (Iterator iterator = paretoRankingList_.iterator(); iterator
			.hasNext();) {
				NameAndRankArray nr = (NameAndRankArray) iterator.next();
				// 指標 rankMaker_[i] に関するログイン名 name のランク．
				int rank = ((Integer) (tmpMap.get(nr.getName()))).intValue();
				nr.getRankArray().setRankAt(i, rank);
			}
		}
	}

	/**
	 * パレートランクのリストを返します．
	 * @return paretoRankingMap パレートランクのリスト
	 */
	public ArrayList getParetoRankingList() {
		return paretoRankingList_;
	}

	/**
	 * 指標に対してランクを作るクラスの数を返します．
	 * @return maxRankMaker 指標に対してランクを作るクラスの数
	 */
	public int getMaxRankMaker() {
		return maxRankMaker_;
	}

	/**
	 * シミュレーション数を返します．
	 * @return simulationNum シミュレーション数
	 */
	public int getSimulationNum() {
		return simulationNum_;
	}

	/**
	 * エージェント数を返します．
	 * @return totalAgentNum エージェント数
	 */
	public int getTotalAgentNum() {
		return totalAgentNum_;
	}

	/**
	 * パレートランキングを計算します．
	 */
	public void calcParetoRank() {
		//      エージェントがいなければやることなし．
		if (getTotalAgentNum() == 0) {
			return;
		}
		//      エージェントが一匹ならここで終り．
		if (getTotalAgentNum() == 1) {
			NameAndRankArray nr0 = (NameAndRankArray) paretoRankingList_.get(0);
			RankArray preRankArray = nr0.getRankArray();
			nr0.setParetoRank(1);
			return;
		}

		int currentRank = 1;
		boolean[] alreadySelected = new boolean[paretoRankingList_.size()];
		Arrays.fill(alreadySelected, false);
		//2 匹以上のとき

		while (isContainsFalse(alreadySelected) == true) { // まだ選ばれていない奴がいる．
			ArrayList numArray = new ArrayList(); // 今回選ぶ番号を保存する．
			for (int i = 0; i < paretoRankingList_.size(); i++) {
				if (alreadySelected[i] == true) {
					//既に選ばれている．
					continue;
				}
				boolean isDominated = false; // まずは優越されていないと考える．
				NameAndRankArray nr = (NameAndRankArray) paretoRankingList_
				.get(i);
				RankArray rankArray = nr.getRankArray();
				for (int j = 0; j < paretoRankingList_.size(); j++) {
					if (alreadySelected[j] == true || i == j) {
						//既に選ばれているか自分自身との比較時．
						continue;
					}
					NameAndRankArray nrTarget = (NameAndRankArray) paretoRankingList_
					.get(j);
					RankArray rankArrayTarget = nrTarget.getRankArray();
					//もしも，一回でも Target に優越されてしまえば終り．
					// 弱パレートを同ランクにすると，破産率がほとんど 0 なので皆同ランクになってしまう．
					if (rankArrayTarget.isDominate(rankArray)) {
						isDominated = true;
						break;
					}
				}
				// 現在残っている誰にも優越されていなければ，次の候補．
				if (isDominated == false) {
					numArray.add(new Integer(i));
				}
			}
			// 必ず，ひとつは選ばれるはず．選ばれていないときは無限ループに落ちるので強制終了．
			if(numArray.size() == 0){
				System.err.println("Something is wrong! Infinite loop was happed in calcParetoRank(), ParetoRanking.java");
				System.exit(-1);
			}
			Iterator iter = numArray.iterator();
			while (iter.hasNext()) {
				Integer num = (Integer) iter.next();
				// ランクをセットして，選ばれたフラグを立てる．
				NameAndRankArray nr = (NameAndRankArray) paretoRankingList_
				.get(num.intValue());
				nr.setParetoRank(currentRank);
				alreadySelected[num.intValue()] = true;
			}
			currentRank += numArray.size(); // ランクを今回選んだ分だけ増やす．
		}
	}

	private boolean isContainsFalse(boolean[] array) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == false) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 画面に結果を出力します．
	 *
	 */
	public void printResult(){
		StringBuffer usingIndex = new StringBuffer();
		for (int i = 0; i < rankMaker_.length; i++) {
			usingIndex.append(rankMaker_[i].getName()+",");
		}
		usingIndex.deleteCharAt(usingIndex.lastIndexOf(","));
		System.out.println("ParetoRank,AgentName,"+usingIndex);
		Collections.sort(paretoRankingList_);
		Iterator iter = paretoRankingList_.iterator();
		while (iter.hasNext()) {
			NameAndRankArray element = (NameAndRankArray) iter.next();
			System.out.println(element);
		}
	}

	/**
	 * 出力ストリームに結果を出力します．
	 * @param pw 出力ストリーム
	 */
	public void printResult(PrintWriter pw){
		StringBuffer usingIndex = new StringBuffer();
		for (int i = 0; i < rankMaker_.length; i++) {
			usingIndex.append(rankMaker_[i].getName()+",");
		}
		usingIndex.deleteCharAt(usingIndex.lastIndexOf(","));
		pw.println("ParetoRank,AgentName,"+usingIndex);
		Collections.sort(paretoRankingList_);
		Iterator iter = paretoRankingList_.iterator();
		while (iter.hasNext()) {
			NameAndRankArray element = (NameAndRankArray) iter.next();
			pw.println(element);
		}
	}

	/**
	 * メインメソッドです．
	 * @param args 実験ディレクトリ
	 */
	public static void main(String[] args) {
		if(args.length < 1){
			System.err.println("Usage: java analysis.ParetoRanking dir1 dir2 ... ");
			System.exit(1);
		}
		ParetoRanking pr = new ParetoRanking(args);
		pr.calcParetoRank();
		pr.printResult();
	}
}
