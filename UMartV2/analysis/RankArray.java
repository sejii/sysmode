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

import java.util.Arrays;

/**
 * ランクを保存する配列のクラスです．
 */
public class RankArray{
	
	/** ランクを保存する配列 */
	private int[] rank_;
	
	/** 保存するランク数 */
	private int size_ = 0;

	/**
	 * コンストラクタです．
	 * @param size ランクの数
	 */
	public RankArray(int size){
		size_ = size;
		rank_ = new int[size_];
		Arrays.fill(rank_,0);
	}
	
	/**
	 * すべてのランクが優れている場合をチェックします．
	 * ひとつでもランクが悪いか等しければfalseを返すので，弱パレート用になっています．
	 * @param oa ランクの配列
	 * @return true:すべてのランクが優れている，false:ひとつでもランクが悪いか等しい
	 */
	public boolean isDominateForWeakPareto(RankArray oa){
		for (int i = 0; i < getSize(); i++) {
			// ひとつでもランクが悪いか等しければだめ．
			if( getRankAt(i) >= oa.getRankAt(i)){
				return false;
			}
		}
		return true;
	}

//	自身が oa に優越しているか？     
	
	/**
	 * 自身がoaよりも優越しているかをチェックします．
	 * @param oa 比較対象
	 * @return true:優越している, false:優越していない
	 */
	public boolean isDominate(RankArray oa){
		//すべて等しいときチェック用
		boolean isSuperior = false;
		for (int i = 0; i < getSize(); i++) {
			// ひとつでもランクが悪ければ優劣ではない．
			if( getRankAt(i) > oa.getRankAt(i)){
				return false;
			}
			//優劣であるためには少なくともひとつはランクが上．
			if(getRankAt(i) < oa.getRankAt(i)){
				isSuperior = true;
			}            
		}
		if(isSuperior){
			// 劣ったランクがなく，少なくともひとつは優れている．
			return true;
		}else{
			// 両者が完全に等しい場合．
			return false;
		}
	}    

	/**
	 * i番目のランクを返す．
	 * @param i 添え字
	 * @return rank ランク
	 */
	public int getRankAt(int i) {
		return rank_[i];
	}
	
	/**
	 * i番目にランクを設定する．
	 * @param index 添え字
	 * @param rank ランク
	 */
	public void setRankAt(int index, int rank) {
		rank_[index]=rank;
	}
	
	/**
	 * 配列の大きさを返す．
	 * @return size 配列の大きさ
	 */
	public int getSize() {
		return size_;
	}
	
	/**
	 * 配列の大きさを設定する．
	 * @param size　配列の大きさ
	 */
	public void setSize(int size) {
		size_ = size;
		rank_ = new int[size];
		Arrays.fill(rank_,0);        
	}
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < rank_.length; i++) {
			sb.append(rank_[i]+",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o){
		RankArray or = (RankArray)o;
		for (int i = 0; i < or.getSize(); i++) {
			if(or.getRankAt(i)!=getRankAt(i)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ランクの配列を返す．
	 * @return ランクの配列
	 */
	public int[] getRank() {
		return rank_;
	}
	
	/**
	 * ランクの配列を設定する．
	 * @param rank ランクの配列
	 */
	public void setRank(int[] rank) {
		rank_ = rank;
		size_ = rank.length;
	}
	
}

