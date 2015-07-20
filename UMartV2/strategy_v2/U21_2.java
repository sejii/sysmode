package strategy_v2 ;

 import static java.lang.Math.*;

import java.util.Random;

 public class U21_2 extends UAgent {

 public static final int DEFAULT_WIDTH_OF_PRICE = 20;
 public static final int DEFAULT_MAX_QUANT = 50;
 public static final int DEFAULT_MIN_QUANT = 10;
 public static final int DEFAULT_MAX_POSITION = 300;
 public static final int DEFAULT_NOMINAL_PRICE = 3000;

 private int fWidthOfPrice = DEFAULT_WIDTH_OF_PRICE ;
 private int fMaxQuant = DEFAULT_MAX_QUANT ;
 private int fMinQuant = DEFAULT_MIN_QUANT ;
 private int fMaxPosition = DEFAULT_MAX_POSITION ;
 private int fNominalPrice = DEFAULT_NOMINAL_PRICE ;

 public static final String WIDTH_OF_PRICE_KEY = " WidthOfPrice " ;
 public static final String MAX_QUANT_KEY = " MaxQuant ";
 public static final String MIN_QUANT_KEY = " MinQuant ";
 public static final String MAX_POSITION_KEY = " MaxPosition " ;
 public static final String NOMINAL_PRICE_KEY = " NominalPrice ";

 public U21_2 ( String loginName , String passwd , String realName , int seed ) {
 super ( loginName , passwd , realName , seed );
 }

 public int getWidthOfPrice () {
 return fWidthOfPrice ;
 }

 public int getMinQuant () {
 return fMinQuant ;
 }

 public int getMaxQuant () {
 return fMaxQuant ;
 }

 public int getMaxPosition () {
 return fMaxPosition ;
 }

 public int getNominalPrice () {
 return fNominalPrice ;
 }


 public UOrderForm [] makeOrderForms ( int day , int session ,
	 int maxDays , int noOfSessionsPerDay ,
	 int [] spotPrices , int [] futurePrices ,
	 int position , long money ) {
	 Random rand = getRandom ();

	 //判定アルゴリズム
	 //高速フーリエ変換(FFT)を行い、周波数成分の一番高いものをこの市場の周期として採用。
	 //FFTのアルゴリズムは以下のページのものを使用した：
	 //http://hp.vector.co.jp/authors/VA046927/fft4gjava.html

	 //個体数Nの決定
	 //futurePrices.length以上の一番最初に来る2の冪乗の値とする
	 int n = 1;
	 for(int i=0; i<100; i++) {
		 if(futurePrices.length<Math.pow(i, 2)) {
			 n=(int)Math.pow(i,2);
			 break;
		 }
	 }

	 //変数の初期化
	 //futurePrices[i]の値をコピーする。
	 //値が-1であれば直前の値をコピーする。
	 FFT4g fft = new FFT4g(n);
	 double[] spectrum = new double[n];
	 for(int i=0; i<spectrum.length; i++) {
		 if(i==0) {
			 if (futurePrices[0]<0)
				 spectrum[0]=0;
			 else spectrum[0]=futurePrices[0];
			 continue;
		 }
		 if(i<futurePrices.length) {
			 if(futurePrices[i]<0)
				 spectrum[i]=spectrum[i-1];
			 else spectrum[i]=futurePrices[i];
		 }
		 else spectrum[i]=0;
	 }

	 //fftの実行
	 fft.rdft(1, spectrum);

	 //6以上の周波数で一番大きい成分を株価の変動周期として採用
	 double max=0;
	 int maxNum=-1;
	 double cycle;
	 for(int i=6; i<n; i++) {
		 if(Math.abs(spectrum[i])>Math.abs(max)) {
			 max = spectrum[i];
			 maxNum = i+1;
		 }
	 }
	 cycle=(double)n/maxNum; //{cycle}節経つと一周する

	 //得られた周期を元に変動予測をする
	 double radian;
	 double nowValue;
	 double nextValue;
	 double diff;
	 if(max>=0)
		 radian = futurePrices.length % cycle / cycle * 2 * Math.PI;
	 else
		 radian = (1 - futurePrices.length % cycle / cycle) * 2 * Math.PI;
	 nowValue = Math.sin(radian);
	 if(max>=0)
		 radian = (futurePrices.length+1) % cycle / cycle * 2 * Math.PI;
	 else
		 radian = (1 - (futurePrices.length+1) % cycle / cycle) * 2 * Math.PI;
	 nextValue = Math.sin(radian);
	 diff = nextValue - nowValue;


	 //フォーム作成
	 UOrderForm [] forms = new UOrderForm [1];
	 forms[0] = new UOrderForm();

	 //変動幅が0.3以上なら買い
	 //変動幅が-0.3以下なら売り
	 //それ以外なら何もしない
	 if(diff>=0.3)
		 forms[0].setBuySell(UOrderForm.BUY);
	 else if(diff<=-0.3)
		 forms[0].setBuySell(UOrderForm.SELL);
	 else {
		 forms[0].setBuySell(UOrderForm.NONE);
		 return forms;
	 }

	 //ポジション許容値内で取引を行う
	 if ( forms [0]. getBuySell () == UOrderForm . BUY ) {
		 if ( position > fMaxPosition ) {
		 forms [0]. setBuySell ( UOrderForm . NONE );
		 return forms ;
		 }
		 } else if ( forms [0]. getBuySell () == UOrderForm . SELL ) {
		 if ( position < - fMaxPosition ) {
		 forms [0]. setBuySell ( UOrderForm . NONE );
		 return forms ;
		 }
		 }

	 //価格設定はUMovingAverageAgentと同じ
	 forms [0]. setPrice (determinePrice(forms[0].getBuySell(),spotPrices));
	 //個数は変動差分を用いる
	 forms [0]. setQuantity ( (int) (fMinQuant + (fMaxQuant - fMinQuant)*Math.abs(diff)));

	 println ( " day =" + day + " , session =" + session
			 + " , " + forms [0]. getBuySellByString () + " , price =" + forms [0]. getPrice ()
			 + " , quantity =" + forms [0]. getQuantity ());
	 return forms ;
 }

	int determinePrice(int action, int[] prices) {
		 Random rand = getRandom();
		 int averagePrice = Math.abs(prices[prices.length-1]-prices[prices.length-2]);
		 int rnd = ( int )(( double ) (averagePrice/4) * rand . nextGaussian ());
		 int price = prices[prices.length-1];
		 if(action==UOrderForm.BUY)
			 price+=rnd;
		 else
			 price-=rnd;

		 if ( price <= 0) {
		 price = 1;
		 }
		 return price;
	}

 public void setParameters ( String [] args ) {
 super . setParameters ( args );
 for ( int i = 0; i < args . length ; ++ i ) {
 String [] strArray = args [i]. split ("=");
 String key = strArray [0];
 String value = strArray [1];
 if ( key . equals ( U21_2 . WIDTH_OF_PRICE_KEY )) {
 fWidthOfPrice = Integer . parseInt (value);
 println (" WidthOfPrice has been changed to " + fWidthOfPrice );
 } else if ( key . equals ( U21_2 . MIN_QUANT_KEY )) {
 fMinQuant = Integer . parseInt ( value );
 println (" MinQuant has been changed to " + fMinQuant );
 } else if ( key . equals ( U21_2 . MAX_QUANT_KEY )) {
 fMaxQuant = Integer . parseInt ( value );
 println (" MaxQuant has been changed to " + fMaxQuant );
 } else if ( key . equals ( U21_2 . MAX_POSITION_KEY )) {
 fMaxPosition = Integer . parseInt ( value );
 println (" MaxPosition has been changed to " + fMaxPosition );
 } else if ( key . equals ( U21_2 . NOMINAL_PRICE_KEY )) {
 fNominalPrice = Integer . parseInt ( value );
 println (" NominalPrice has been changed to " + fNominalPrice );
 } else {
 println (" Unknown parameter :" + key + " in URandomAgent . setParameters " );
 }
 }
 }


 //##############################
 //###フーリエ変換を行うクラス###############################################
 //###http://hp.vector.co.jp/authors/VA046927/fft4gjava.html のものを使用 ###
 //##########################################################################
 class FFT4g {
		private int[] ip;
		private double[] w;
		private int n;

		FFT4g(int n) {
			this.n = n;
			ip = new int[2+(int)Math.sqrt((double)n/2.0)+1];
			w = new double[n/2];
			ip[0] = 0;
		}

		public void rdft(int isgn, double[] a)
		{
		    int nw, nc;
		    double xi;

		    nw = ip[0];
		    if (n > (nw << 2)) {
		        nw = n >> 2;
		        makewt(nw);
		    }
		    nc = ip[1];
		    if (n > (nc << 2)) {
		        nc = n >> 2;
		        makect(nc, w, nw);
		    }
		    if (isgn >= 0) {
		        if (n > 4) {
		            bitrv2(n, a);
		            cftfsub(a);
		            rftfsub(a, nc, w, nw);
		        } else if (n == 4) {
		            cftfsub(a);
		        }
		        xi = a[0] - a[1];
		        a[0] += a[1];
		        a[1] = xi;
		    } else {
		        a[1] = 0.5 * (a[0] - a[1]);
		        a[0] -= a[1];
		        if (n > 4) {
		            rftbsub(a, nc, w, nw);
		            bitrv2(n, a);
		            cftbsub(a);
		        } else if (n == 4) {
		            cftfsub(a);
		        }
		    }
		}

		private void makewt(int nw)
		{
		    int j, nwh;
		    double delta, x, y;

		    ip[0] = nw;
		    ip[1] = 1;
		    if (nw > 2) {
		        nwh = nw >> 1;
		        delta = atan(1.0) / nwh;
		        w[0] = 1;
		        w[1] = 0;
		        w[nwh] = cos(delta * nwh);
		        w[nwh + 1] = w[nwh];
		        if (nwh > 2) {
		            for (j = 2; j < nwh; j += 2) {
		                x = cos(delta * j);
		                y = sin(delta * j);
		                w[j] = x;
		                w[j + 1] = y;
		                w[nw - j] = y;
		                w[nw - j + 1] = x;
		            }
		            bitrv2(nw, w);
		        }
		    }
		}

		void makect(int nc, double[] c, int nw)
		{
		    int j, nch;
		    double delta;

		    ip[1] = nc;
		    if (nc > 1) {
		        nch = nc >> 1;
		        delta = atan(1.0) / nch;
		        c[nw + 0] = cos(delta * nch);
		        c[nw + nch] = 0.5 * c[nw + 0];
		        for (j = 1; j < nch; j++) {
		            c[nw + j] = 0.5 * cos(delta * j);
		            c[nw + nc - j] = 0.5 * sin(delta * j);
		        }
		    }
		}

		/* -------- child routines -------- */

		private void bitrv2(int n, double[] a)
		{
		    int j, j1, k, k1, l, m, m2;
		    double xr, xi, yr, yi;

		    ip[2 + 0] = 0;
		    l = n;
		    m = 1;
		    while ((m << 3) < l) {
		        l >>= 1;
		        for (j = 0; j < m; j++) {
		            ip[2 + m + j] = ip[2 + j] + l;
		        }
		        m <<= 1;
		    }
		    m2 = 2 * m;
		    if ((m << 3) == l) {
		        for (k = 0; k < m; k++) {
		            for (j = 0; j < k; j++) {
		                j1 = 2 * j + ip[2 + k];
		                k1 = 2 * k + ip[2 + j];
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		                j1 += m2;
		                k1 += 2 * m2;
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		                j1 += m2;
		                k1 -= m2;
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		                j1 += m2;
		                k1 += 2 * m2;
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		            }
		            j1 = 2 * k + m2 + ip[2 + k];
		            k1 = j1 + m2;
		            xr = a[j1];
		            xi = a[j1 + 1];
		            yr = a[k1];
		            yi = a[k1 + 1];
		            a[j1] = yr;
		            a[j1 + 1] = yi;
		            a[k1] = xr;
		            a[k1 + 1] = xi;
		        }
		    } else {
		        for (k = 1; k < m; k++) {
		            for (j = 0; j < k; j++) {
		                j1 = 2 * j + ip[2 + k];
		                k1 = 2 * k + ip[2 + j];
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		                j1 += m2;
		                k1 += m2;
		                xr = a[j1];
		                xi = a[j1 + 1];
		                yr = a[k1];
		                yi = a[k1 + 1];
		                a[j1] = yr;
		                a[j1 + 1] = yi;
		                a[k1] = xr;
		                a[k1 + 1] = xi;
		            }
		        }
		    }
		}

		private void rftfsub(double[] a, int nc, double[] c, int nw)
		{
		    int j, k, kk, ks, m;
		    double wkr, wki, xr, xi, yr, yi;

		    m = n >> 1;
		    ks = 2 * nc / m;
		    kk = 0;
		    for (j = 2; j < m; j += 2) {
		        k = n - j;
		        kk += ks;
		        wkr = 0.5 - c[nw + nc - kk];
		        wki = c[nw + kk];
		        xr = a[j] - a[k];
		        xi = a[j + 1] + a[k + 1];
		        yr = wkr * xr - wki * xi;
		        yi = wkr * xi + wki * xr;
		        a[j] -= yr;
		        a[j + 1] -= yi;
		        a[k] += yr;
		        a[k + 1] -= yi;
		    }
		}

		private void rftbsub(double[] a, int nc, double[] c, int nw)
		{
		    int j, k, kk, ks, m;
		    double wkr, wki, xr, xi, yr, yi;

		    a[1] = -a[1];
		    m = n >> 1;
		    ks = 2 * nc / m;
		    kk = 0;
		    for (j = 2; j < m; j += 2) {
		        k = n - j;
		        kk += ks;
		        wkr = 0.5 - c[nw + nc - kk];
		        wki = c[nw + kk];
		        xr = a[j] - a[k];
		        xi = a[j + 1] + a[k + 1];
		        yr = wkr * xr + wki * xi;
		        yi = wkr * xi - wki * xr;
		        a[j] -= yr;
		        a[j + 1] = yi - a[j + 1];
		        a[k] += yr;
		        a[k + 1] = yi - a[k + 1];
		    }
		    a[m + 1] = -a[m + 1];
		}

		private void cftfsub(double[] a)
		{
		    int j, j1, j2, j3, l;
		    double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		    l = 2;
		    if (n > 8) {
		        cft1st(a);
		        l = 8;
		        while ((l << 2) < n) {
		            cftmdl(l, a);
		            l <<= 2;
		        }
		    }
		    if ((l << 2) == n) {
		        for (j = 0; j < l; j += 2) {
		            j1 = j + l;
		            j2 = j1 + l;
		            j3 = j2 + l;
		            x0r = a[j] + a[j1];
		            x0i = a[j + 1] + a[j1 + 1];
		            x1r = a[j] - a[j1];
		            x1i = a[j + 1] - a[j1 + 1];
		            x2r = a[j2] + a[j3];
		            x2i = a[j2 + 1] + a[j3 + 1];
		            x3r = a[j2] - a[j3];
		            x3i = a[j2 + 1] - a[j3 + 1];
		            a[j] = x0r + x2r;
		            a[j + 1] = x0i + x2i;
		            a[j2] = x0r - x2r;
		            a[j2 + 1] = x0i - x2i;
		            a[j1] = x1r - x3i;
		            a[j1 + 1] = x1i + x3r;
		            a[j3] = x1r + x3i;
		            a[j3 + 1] = x1i - x3r;
		        }
		    } else {
		        for (j = 0; j < l; j += 2) {
		            j1 = j + l;
		            x0r = a[j] - a[j1];
		            x0i = a[j + 1] - a[j1 + 1];
		            a[j] += a[j1];
		            a[j + 1] += a[j1 + 1];
		            a[j1] = x0r;
		            a[j1 + 1] = x0i;
		        }
		    }
		}

		private void cftbsub(double[] a)
		{
		    int j, j1, j2, j3, l;
		    double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		    l = 2;
		    if (n > 8) {
		        cft1st(a);
		        l = 8;
		        while ((l << 2) < n) {
		            cftmdl(l, a);
		            l <<= 2;
		        }
		    }
		    if ((l << 2) == n) {
		        for (j = 0; j < l; j += 2) {
		            j1 = j + l;
		            j2 = j1 + l;
		            j3 = j2 + l;
		            x0r = a[j] + a[j1];
		            x0i = -a[j + 1] - a[j1 + 1];
		            x1r = a[j] - a[j1];
		            x1i = -a[j + 1] + a[j1 + 1];
		            x2r = a[j2] + a[j3];
		            x2i = a[j2 + 1] + a[j3 + 1];
		            x3r = a[j2] - a[j3];
		            x3i = a[j2 + 1] - a[j3 + 1];
		            a[j] = x0r + x2r;
		            a[j + 1] = x0i - x2i;
		            a[j2] = x0r - x2r;
		            a[j2 + 1] = x0i + x2i;
		            a[j1] = x1r - x3i;
		            a[j1 + 1] = x1i - x3r;
		            a[j3] = x1r + x3i;
		            a[j3 + 1] = x1i + x3r;
		        }
		    } else {
		        for (j = 0; j < l; j += 2) {
		            j1 = j + l;
		            x0r = a[j] - a[j1];
		            x0i = -a[j + 1] + a[j1 + 1];
		            a[j] += a[j1];
		            a[j + 1] = -a[j + 1] - a[j1 + 1];
		            a[j1] = x0r;
		            a[j1 + 1] = x0i;
		        }
		    }
		}

		private void cft1st(double[] a)
		{
		    int j, k1, k2;
		    double wk1r, wk1i, wk2r, wk2i, wk3r, wk3i;
		    double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		    x0r = a[0] + a[2];
		    x0i = a[1] + a[3];
		    x1r = a[0] - a[2];
		    x1i = a[1] - a[3];
		    x2r = a[4] + a[6];
		    x2i = a[5] + a[7];
		    x3r = a[4] - a[6];
		    x3i = a[5] - a[7];
		    a[0] = x0r + x2r;
		    a[1] = x0i + x2i;
		    a[4] = x0r - x2r;
		    a[5] = x0i - x2i;
		    a[2] = x1r - x3i;
		    a[3] = x1i + x3r;
		    a[6] = x1r + x3i;
		    a[7] = x1i - x3r;
		    wk1r = w[2];
		    x0r = a[8] + a[10];
		    x0i = a[9] + a[11];
		    x1r = a[8] - a[10];
		    x1i = a[9] - a[11];
		    x2r = a[12] + a[14];
		    x2i = a[13] + a[15];
		    x3r = a[12] - a[14];
		    x3i = a[13] - a[15];
		    a[8] = x0r + x2r;
		    a[9] = x0i + x2i;
		    a[12] = x2i - x0i;
		    a[13] = x0r - x2r;
		    x0r = x1r - x3i;
		    x0i = x1i + x3r;
		    a[10] = wk1r * (x0r - x0i);
		    a[11] = wk1r * (x0r + x0i);
		    x0r = x3i + x1r;
		    x0i = x3r - x1i;
		    a[14] = wk1r * (x0i - x0r);
		    a[15] = wk1r * (x0i + x0r);
		    k1 = 0;
		    for (j = 16; j < n; j += 16) {
		        k1 += 2;
		        k2 = 2 * k1;
		        wk2r = w[k1];
		        wk2i = w[k1 + 1];
		        wk1r = w[k2];
		        wk1i = w[k2 + 1];
		        wk3r = wk1r - 2 * wk2i * wk1i;
		        wk3i = 2 * wk2i * wk1r - wk1i;
		        x0r = a[j] + a[j + 2];
		        x0i = a[j + 1] + a[j + 3];
		        x1r = a[j] - a[j + 2];
		        x1i = a[j + 1] - a[j + 3];
		        x2r = a[j + 4] + a[j + 6];
		        x2i = a[j + 5] + a[j + 7];
		        x3r = a[j + 4] - a[j + 6];
		        x3i = a[j + 5] - a[j + 7];
		        a[j] = x0r + x2r;
		        a[j + 1] = x0i + x2i;
		        x0r -= x2r;
		        x0i -= x2i;
		        a[j + 4] = wk2r * x0r - wk2i * x0i;
		        a[j + 5] = wk2r * x0i + wk2i * x0r;
		        x0r = x1r - x3i;
		        x0i = x1i + x3r;
		        a[j + 2] = wk1r * x0r - wk1i * x0i;
		        a[j + 3] = wk1r * x0i + wk1i * x0r;
		        x0r = x1r + x3i;
		        x0i = x1i - x3r;
		        a[j + 6] = wk3r * x0r - wk3i * x0i;
		        a[j + 7] = wk3r * x0i + wk3i * x0r;
		        wk1r = w[k2 + 2];
		        wk1i = w[k2 + 3];
		        wk3r = wk1r - 2 * wk2r * wk1i;
		        wk3i = 2 * wk2r * wk1r - wk1i;
		        x0r = a[j + 8] + a[j + 10];
		        x0i = a[j + 9] + a[j + 11];
		        x1r = a[j + 8] - a[j + 10];
		        x1i = a[j + 9] - a[j + 11];
		        x2r = a[j + 12] + a[j + 14];
		        x2i = a[j + 13] + a[j + 15];
		        x3r = a[j + 12] - a[j + 14];
		        x3i = a[j + 13] - a[j + 15];
		        a[j + 8] = x0r + x2r;
		        a[j + 9] = x0i + x2i;
		        x0r -= x2r;
		        x0i -= x2i;
		        a[j + 12] = -wk2i * x0r - wk2r * x0i;
		        a[j + 13] = -wk2i * x0i + wk2r * x0r;
		        x0r = x1r - x3i;
		        x0i = x1i + x3r;
		        a[j + 10] = wk1r * x0r - wk1i * x0i;
		        a[j + 11] = wk1r * x0i + wk1i * x0r;
		        x0r = x1r + x3i;
		        x0i = x1i - x3r;
		        a[j + 14] = wk3r * x0r - wk3i * x0i;
		        a[j + 15] = wk3r * x0i + wk3i * x0r;
		    }
		}

		private void cftmdl(int l, double[] a)
		{
		    int j, j1, j2, j3, k, k1, k2, m, m2;
		    double wk1r, wk1i, wk2r, wk2i, wk3r, wk3i;
		    double x0r, x0i, x1r, x1i, x2r, x2i, x3r, x3i;

		    m = l << 2;
		    for (j = 0; j < l; j += 2) {
		        j1 = j + l;
		        j2 = j1 + l;
		        j3 = j2 + l;
		        x0r = a[j] + a[j1];
		        x0i = a[j + 1] + a[j1 + 1];
		        x1r = a[j] - a[j1];
		        x1i = a[j + 1] - a[j1 + 1];
		        x2r = a[j2] + a[j3];
		        x2i = a[j2 + 1] + a[j3 + 1];
		        x3r = a[j2] - a[j3];
		        x3i = a[j2 + 1] - a[j3 + 1];
		        a[j] = x0r + x2r;
		        a[j + 1] = x0i + x2i;
		        a[j2] = x0r - x2r;
		        a[j2 + 1] = x0i - x2i;
		        a[j1] = x1r - x3i;
		        a[j1 + 1] = x1i + x3r;
		        a[j3] = x1r + x3i;
		        a[j3 + 1] = x1i - x3r;
		    }
		    wk1r = w[2];
		    for (j = m; j < l + m; j += 2) {
		        j1 = j + l;
		        j2 = j1 + l;
		        j3 = j2 + l;
		        x0r = a[j] + a[j1];
		        x0i = a[j + 1] + a[j1 + 1];
		        x1r = a[j] - a[j1];
		        x1i = a[j + 1] - a[j1 + 1];
		        x2r = a[j2] + a[j3];
		        x2i = a[j2 + 1] + a[j3 + 1];
		        x3r = a[j2] - a[j3];
		        x3i = a[j2 + 1] - a[j3 + 1];
		        a[j] = x0r + x2r;
		        a[j + 1] = x0i + x2i;
		        a[j2] = x2i - x0i;
		        a[j2 + 1] = x0r - x2r;
		        x0r = x1r - x3i;
		        x0i = x1i + x3r;
		        a[j1] = wk1r * (x0r - x0i);
		        a[j1 + 1] = wk1r * (x0r + x0i);
		        x0r = x3i + x1r;
		        x0i = x3r - x1i;
		        a[j3] = wk1r * (x0i - x0r);
		        a[j3 + 1] = wk1r * (x0i + x0r);
		    }
		    k1 = 0;
		    m2 = 2 * m;
		    for (k = m2; k < n; k += m2) {
		        k1 += 2;
		        k2 = 2 * k1;
		        wk2r = w[k1];
		        wk2i = w[k1 + 1];
		        wk1r = w[k2];
		        wk1i = w[k2 + 1];
		        wk3r = wk1r - 2 * wk2i * wk1i;
		        wk3i = 2 * wk2i * wk1r - wk1i;
		        for (j = k; j < l + k; j += 2) {
		            j1 = j + l;
		            j2 = j1 + l;
		            j3 = j2 + l;
		            x0r = a[j] + a[j1];
		            x0i = a[j + 1] + a[j1 + 1];
		            x1r = a[j] - a[j1];
		            x1i = a[j + 1] - a[j1 + 1];
		            x2r = a[j2] + a[j3];
		            x2i = a[j2 + 1] + a[j3 + 1];
		            x3r = a[j2] - a[j3];
		            x3i = a[j2 + 1] - a[j3 + 1];
		            a[j] = x0r + x2r;
		            a[j + 1] = x0i + x2i;
		            x0r -= x2r;
		            x0i -= x2i;
		            a[j2] = wk2r * x0r - wk2i * x0i;
		            a[j2 + 1] = wk2r * x0i + wk2i * x0r;
		            x0r = x1r - x3i;
		            x0i = x1i + x3r;
		            a[j1] = wk1r * x0r - wk1i * x0i;
		            a[j1 + 1] = wk1r * x0i + wk1i * x0r;
		            x0r = x1r + x3i;
		            x0i = x1i - x3r;
		            a[j3] = wk3r * x0r - wk3i * x0i;
		            a[j3 + 1] = wk3r * x0i + wk3i * x0r;
		        }
		        wk1r = w[k2 + 2];
		        wk1i = w[k2 + 3];
		        wk3r = wk1r - 2 * wk2r * wk1i;
		        wk3i = 2 * wk2r * wk1r - wk1i;
		        for (j = k + m; j < l + (k + m); j += 2) {
		            j1 = j + l;
		            j2 = j1 + l;
		            j3 = j2 + l;
		            x0r = a[j] + a[j1];
		            x0i = a[j + 1] + a[j1 + 1];
		            x1r = a[j] - a[j1];
		            x1i = a[j + 1] - a[j1 + 1];
		            x2r = a[j2] + a[j3];
		            x2i = a[j2 + 1] + a[j3 + 1];
		            x3r = a[j2] - a[j3];
		            x3i = a[j2 + 1] - a[j3 + 1];
		            a[j] = x0r + x2r;
		            a[j + 1] = x0i + x2i;
		            x0r -= x2r;
		            x0i -= x2i;
		            a[j2] = -wk2i * x0r - wk2r * x0i;
		            a[j2 + 1] = -wk2i * x0i + wk2r * x0r;
		            x0r = x1r - x3i;
		            x0i = x1i + x3r;
		            a[j1] = wk1r * x0r - wk1i * x0i;
		            a[j1 + 1] = wk1r * x0i + wk1i * x0r;
		            x0r = x1r + x3i;
		            x0i = x1i - x3r;
		            a[j3] = wk3r * x0r - wk3i * x0i;
		            a[j3 + 1] = wk3r * x0i + wk3i * x0r;
		        }
		    }
		}
	}
 }