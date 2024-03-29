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
import java.util.HashMap;
import java.util.Iterator;

/**
 * 破産しなかった試行の割合（非破産率）に基づいてランクを計算するクラスです．
 *
 */
public class BankruptcyRateRankMaker extends AbstractRankMaker{
	
	/**
	 * @see AbstractRankMaker#getName()
	 */
	public String getName(){
		return "BankruptcyRate";
	}
	
	/**
	 * @see AbstractRankMaker#getRank()
	 */
	public HashMap getRank() {
		HashMap result = new HashMap();
		ArrayList allName = getAllLoginName();
		double[] bankruptcyRate = new double[allName.size()];
		int[] rank = new int[allName.size()];
		Iterator iter = allName.iterator();
		int index = 0;
		while (iter.hasNext()) {
			String name = (String) iter.next();
			ArrayList array = getAllValueOf(name,"Status");
			double sum = 0;
			Iterator iter2 = array.iterator();
			while (iter2.hasNext()) {
				String s = (String) iter2.next();
				if(Integer.parseInt(s)==0){
					sum++;
				}
			}
			bankruptcyRate[index] = sum/array.size();
			index++;
		}
		rank = convertToRank(bankruptcyRate,false); // 昇順
		for (int i = 0; i < rank.length; i++) {
			result.put(allName.get(i),new Integer(rank[i]));
		}
		return result;
	}

}
