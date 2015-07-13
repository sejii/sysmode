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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * 結果のハッシュマップから，ひとつの指標に対してランクを作るクラスです．
 */
public abstract class AbstractRankMaker {
	/** su を含めるか？ */
	private boolean isSuValid = false;

	/** HashMap(あるユーザの結果)の ArrayList(一回の試行の全結果)の ArrayList */ 
	private ArrayList allResult_ = null;

	/**
	 * 名前を返します．
	 */ 
	abstract public String getName();

	/**
	 * ランクを返します．
	 * @return ユーザ名を key ランクを value とする HashMap
	 */
	abstract public HashMap getRank();

	/**
	 * 全てのログインユーザを返します．
	 * @return 全てのログインユーザのリスト
	 */
	public ArrayList getAllLoginName(){
		ArrayList result = new ArrayList();
		Iterator iter1 = allResult_.iterator();
		while (iter1.hasNext()) {
			ArrayList array = (ArrayList) iter1.next();
			Iterator iter2 = array.iterator();
			while (iter2.hasNext()) {
				HashMap map = (HashMap) iter2.next();
				if(result.contains(map.get("LoginName"))){
					continue;
				}
				if(map.get("LoginName").equals("su") && !isSuValid()){
					continue;
				}
				result.add(map.get("LoginName"));
			}
		}
		return result; 
	}

	/**
	 * エージェント名 name の key に関する全値を返します．
	 * @param name エージェント名
	 * @param key キー
	 */
	public ArrayList getAllValueOf(String name, String key){
		ArrayList result = new ArrayList();
		Iterator iter1 = allResult_.iterator();
		while (iter1.hasNext()) {
			ArrayList array = (ArrayList) iter1.next();
			Iterator iter2 = array.iterator();
			while (iter2.hasNext()) {
				HashMap map = (HashMap) iter2.next();
				if(name.equals(map.get("LoginName"))){
					result.add(map.get(key));
				}
			}
		}        
		return result;
	}

	/**
	 * 文字列の数値からなる ArrayList の最大を返します．
	 * @param list 文字列の数値からなるArrayList
	 * @return　最大値
	 */
	public double getMax(ArrayList list){
		if(list == null || list.size() == 0){
			return Double.NaN;
		}
		double max = Double.NEGATIVE_INFINITY;
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			double num = Double.parseDouble( (String) iter.next());
			if(num > max){
				max = num;
			}
		}
		return max;
	}

	/**
	 * 文字列の数値からなる ArrayList の最小を返します．
	 * @param list 文字列の数値からなる ArrayList
	 * @return 最小値
	 */    
	public double getMin(ArrayList list){
		if(list == null || list.size() == 0){
			return Double.NaN;
		}
		double min = Double.POSITIVE_INFINITY;
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			double num = Double.parseDouble( (String) iter.next());
			if(num < min){
				min = num;
			}
		}
		return min;
	}

	/**
	 * 文字列の数値からなる ArrayList の平均を返します．
	 * @param list 文字列の数値からなる ArrayList
	 * @return 平均値
	 */    
	public double getMean(ArrayList list){
		if(list == null || list.size() == 0){
			return Double.NaN;
		}
		double mean = 0;
		Iterator iter = list.iterator();
		while (iter.hasNext()) {
			mean += Double.parseDouble( (String) iter.next());
		}
		return mean/list.size();
	}

	/**
	 * 数値配列をランクに変換します．
	 * @param array 数値配列
	 * @param isDesc 降順か
	 * @return rank ランク配列
	 */    
	public int[] convertToRank(double[] array, boolean isDesc){
		if(array == null || array.length == 0){
			return null;
		}
		int[] rank = new int[array.length];
		Arrays.fill(rank,1); // 全ランクを1に
		for (int i = 0; i < array.length-1; i++) {
			for (int j = i+1; j < array.length; j++) {
				if(isDesc){ // 降順の場合．大きい方が良い.
					if(array[i] > array[j]){
						rank[j]++; // 小さい方のランクを増やす．
					}else if(array[i] < array[j]){
						rank[i]++;                        
					}
					// 等しい場合は何もしない．
				}else{ // 昇順の場合．小さい方が良い.
					if(array[i] < array[j]){
						rank[j]++; // 大きい方のランクを増やす．
					}else if(array[i] > array[j]){
						rank[i]++;                        
					}                    
				}
			}
		}
		return rank;
	}    

	/**
	 * 全ての結果を返します．
	 * @return result 結果
	 */
	public ArrayList getAllResult() {
		return allResult_;
	}

	/**
	 * 全ての結果を設定します．
	 * @param result 結果
	 */
	public void setAllResult(ArrayList result) {
		allResult_ = result;
	}

	/**
	 * suを含めるかを返します．
	 * @return isSuValid true: suを含める, false: suを含めない 
	 */
	public boolean isSuValid() {
		return isSuValid;
	}

	/**
	 * suを含めるかを設定します．
	 * @param isSuValid true: suを含める, false: suを含めない
	 */
	public void setSuValid(boolean isSuValid) {
		this.isSuValid = isSuValid;
	}
}
