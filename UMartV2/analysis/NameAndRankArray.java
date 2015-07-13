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

/**
 * ログイン名とランクの配列をまとめたクラスです．
 */
public class NameAndRankArray implements Comparable{

	/** ログイン名 */
	private String name_="";
	
	/** ランクの配列 */
	private RankArray rankArray_=null;
	
	/** 最後に計算するパレートランク．1以上． */
	private int paretoRank_ = 0;

	/**
	 * コンストラクタです．
	 * @param name ログイン名
	 * @param array ランクの配列
	 */
	public NameAndRankArray(String name,RankArray array){
		this.name_ = name;
		this.rankArray_ = array;
	}

	/**
	 * ログイン名を返す．
	 * @return name ログイン名
	 */
	public String getName() {
		return name_;
	}
	/**
	 * ランクの配列
	 * @return rankArray ランクの配列
	 */
	public RankArray getRankArray() {
		return rankArray_;
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return getParetoRank()+","+getName()+","+getRankArray().toString();
	}
	
	/**
	 * パレートランクを返します．
	 * @return paretoRank パレートランク
	 */
	public int getParetoRank() {
		return paretoRank_;
	}
	
	/**
	 * パレートランクを設定します．
	 * @param paretoRank パレートランク
	 */
	public void setParetoRank(int paretoRank) {
		paretoRank_ = paretoRank;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(T)
	 */
	public int compareTo(Object o) {
		NameAndRankArray nr = (NameAndRankArray)o;
		if(getParetoRank() > nr.getParetoRank()){
			return 1;
		}else if(getParetoRank() > nr.getParetoRank()){
			return -1;
		}else {
			return 0;
		}
	}
}
