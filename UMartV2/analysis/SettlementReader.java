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

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Settlement_account.csvを読み込むためのクラスです．
 */
public class SettlementReader {
	
	/** 入力ファイル名 */
	private String fileName_ = "Settlement_account.csv";

	/** 各エージェントの結果が入ったハッシュマップのリスト */
	private ArrayList settlementHashList_ = null;

	/**
	 * コンストラクタです．
	 * @param parentDir 親ディレクトリ名
	 */
	public SettlementReader(String parentDir) {
		init(parentDir);
	}

	/**
	 * コンストラクタです．
	 *
	 */
	public SettlementReader() {
	}

	/**
	 * 初期化メソッドです．
	 * @param parentDir 親ディレクトリ名
	 */
	public void init(String parentDir) {
		settlementHashList_ = new ArrayList();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(parentDir
					+ "/"+fileName_));
			String string;
			// 先頭の一行を読む．ここが key になる．
			string = reader.readLine();
			List keyList = Arrays.asList(string.split(","));
			Iterator iter = keyList.iterator();
			while ((string = reader.readLine()) != null) {
				// 各エージェントの結果
				List valueList = Arrays.asList(string.split(","));
				// 必ず key 文だけ value は必要．
				if (keyList.size() != valueList.size()) {
					System.err.println("keyList.size() != valueList.size()!");
					System.exit(-1);
				}
				HashMap agentResult = new HashMap();
				for (int i = 0; i < keyList.size(); i++) {
					agentResult.put(keyList.get(i), valueList.get(i));
				}
				settlementHashList_.add(agentResult);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * メインメソッドです．
	 * @param args なし
	 */
	public static void main(String[] args) {
		SettlementReader sr = new SettlementReader();
		System.err.println(sr.getSettlementHashList());

	}

	/**
	 * 各エージェントの結果が入ったハッシュマップのリストを返します．
	 * @return settlementHashList 各エージェントの結果が入ったハッシュマップのリスト
	 */
	public ArrayList getSettlementHashList() {
		return settlementHashList_;
	}

	/**
	 * 引数で指定された全ディレクトリの全ての結果が入ったハッシュマップのリストを返します．
	 * @param dirs ディレクトリ
	 * @return 全ディレクトリの全ての結果が入ったハッシュマップのリスト
	 */
	public ArrayList getAllSettlementHashList(String[] dirs){
		ArrayList result = new ArrayList();
		for (int i = 0; i < dirs.length; i++) {
			init(dirs[i]);
			result.add(getSettlementHashList());
		}
		return result;
	}

}
