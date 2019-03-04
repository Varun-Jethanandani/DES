
import java.io.BufferedReader;
import java.util.Random;
import java.io.InputStreamReader;


public class DES {
    
	public int[][] getMatrix(){
		int[][] keyArray = new int[8][7];
		int[] checkDuplicates = new int[56];
		int count = 0;
		Random random = new Random();
		int	x=0;
		for (int i=0;i<8 ;i++ ) {
			for (int j=0;j<7 ;j++ ) {
				boolean dataflag=true;
				while(dataflag){
					boolean flag = true;
					int keyBitIndex = new Random().nextInt(64);
					for (int k=0;k<checkDuplicates.length ;k++ ) {
						if (checkDuplicates[k]==keyBitIndex) {
							flag=false;
						}
					}
					if (keyBitIndex%8!=0 && flag) {
					 	keyArray[i][j] = keyBitIndex;
					 	checkDuplicates[x] = keyBitIndex;
					 	x++;
					 	dataflag = false;
					} 
				}
			}
		}
		return keyArray;
	}

	public char[] getKeyPlus(int[][] keyArray,char[] key){
		char[] keyPlus = new char[56];
    	int x = 0;
    	for (int i=0;i<keyArray.length ;i++ ) {
    		for (int j=0;j<keyArray[i].length ;j++ ) {
    			keyPlus[x] = key[keyArray[i][j]-1];
    			x++;
    		}
    	}
    	return keyPlus;
	}


	public char[] getSubKey(char[] c,char[] d,int[][] subKeyArray){
		char[] cd = new char[56];
		char[] subKey = new char[48];
		int k=0;
		int p=0;
		for (int i=0;i<cd.length ;i++ ) {
			if (i<c.length) {
		 		cd[i] = c[k];
		 		k++;
		 	}else{
		 		cd[i] = d[p];
		 		p++;
		 	} 	
		}
		int x=0;
		for (int i=0;i<subKeyArray.length ;i++ ) {
		 	for (int j=0;j<subKeyArray[i].length ;j++ ) {
		 		subKey[x] = cd[subKeyArray[i][j]-1];
		 		x++;	
		 	}	
		}
		return subKey; 
	}

	public int[] getIterationNumbers(){
		int[] iterationNumber = new int[16];
		for (int i=0;i<iterationNumber.length ;i++ ) {
			boolean flag = true;
			while(flag){
				int noOfLeftShifts = new Random().nextInt(3);
				if (noOfLeftShifts!=0) {
					iterationNumber[i] = noOfLeftShifts;
					flag=false;					
				}
			}
		}
		return iterationNumber;
	}

	public char[] getShiftedArray(int noOfLeftShifts,char[] c0d0){
		char[] cd = new char[28];
		int i = noOfLeftShifts;
		for (int j=0;j<cd.length ;j++ ) {
			if (i==cd.length) {
				i=0;
			}
			cd[j] = c0d0[i];
			i++;
		}
		return cd;		
	}

	public int[] sixTofourBit(int[][] s,int[] b){
		int[] fourBitNumber = new int[4];
		int[] row = new int[2];
		int[] column = new int[4];
		
		row[0] = b[5];
		row[1] = b[0];

		int j=4;
		for (int i=0;i<column.length ;i++ ) {
			column[i] = b[j];
			j--;
		}

		int sRow = 0;
		int sColumn = 0;

		for (int i=0;i<row.length ;i++ ) {
			if (row[i]==1) {
				sRow+=Math.pow(2,i);	
			}
			
		}

		for (int i=0;i<column.length ;i++ ) {
			if (column[i]==1) {
				sColumn+=Math.pow(2,i);	
			}
		}
		int decimalNumber = s[sRow][sColumn];
		for (int i=fourBitNumber.length-1;i>=0 ;i-- ) {
			fourBitNumber[i] = decimalNumber%2;
			decimalNumber = decimalNumber/2;
		}
		return fourBitNumber;
	}

	public String encryption(int[][] ipArray,char[][] subKeys,int[][] ipInverse,String message){
		
		char[] messageBlock = message.toCharArray();
		char[] ip = new char[64];
    	int ipCharIndex = 0;
    	for (int i=0;i<ipArray.length ;i++ ) {
    		for (int j=0;j<ipArray[i].length ;j++ ) {
    			ip[ipCharIndex] = messageBlock[ipArray[i][j]-1];
    			ipCharIndex++; 		
    		}
    	}

    	char[] left = new char[32];
    	char[] right = new char[32];
    	int leftIndex = 0;
    	int rightIndex = 0;
    	for (int i=0;i<ip.length ;i++ ) {
    		if (i<32) {
    			left[leftIndex] = ip[i];
    			leftIndex++;
    		}else{
    	 		right[rightIndex] = ip[i];
    	 		rightIndex++;	
    		}
    	}

    	char[][] l = new char[17][32];
    	char[][] r = new char[17][32];
    	l[0] = left;
    	r[0] = right;

    	for (int i=1;i<l.length ;i++ ) {
    		l[i] = r[i-1];
    		r[i] = getRow(l,r,i-1,subKeys,i,true);	
    	}
    	
    	char[] rightleft = new char[64];
    	int rIndex=0;
    	int lIndex=0;
    	for (int i=0;i<rightleft.length ;i++ ) {
    		if (i<r[16].length) {
    			rightleft[i] =	r[16][rIndex];
    			rIndex++;
    		}else{
    			rightleft[i] = l[16][lIndex];
    			lIndex++;
    		}
    	}

    	char[] encryptedBits = new char[64];
		int encryptedBitIndex = 0;
    	for (int i=0;i<ipInverse.length ;i++ ) {
    	 	for (int x=0;x<ipInverse[i].length ;x++ ) {
    	 		encryptedBits[encryptedBitIndex] = 	rightleft[ipInverse[i][x]-1];
    	 		encryptedBitIndex++;				
			}					
		} 						
		String hexEncryptedMessage = getHexMessage(encryptedBits);
		return hexEncryptedMessage;
	}

	public String decryption(int[][] ipArray,char[][] subKeys,int[][] ipInverse,String hexEncryptedMessage){
		String binarydecryptedBits = "";
		for (int i=0;i<hexEncryptedMessage.length() ;i++ ) {
			binarydecryptedBits+=String.format("%4s",(Integer.toBinaryString(Integer.parseInt(hexEncryptedMessage.substring(i,i+1),16)))).replace(' ','0');
		}
		
		char[] decryptedrightleft = new char[64];
		int rlIndex = 0;
		for (int i=0;i<ipInverse.length ;i++ ) {
    	 	for (int x=0;x<ipInverse[i].length ;x++ ) {
    	 		decryptedrightleft[ipInverse[i][x]-1] = binarydecryptedBits.charAt(rlIndex);
    	 		rlIndex++;				
			}					
		}

		char[] decryptright = new char[32];
		char[] decryptleft = new char[32]; 
		int leftIndex = 0;
		for (int i=0;i<decryptedrightleft.length ;i++ ) {
			if (i<32) {
				decryptright[i] = decryptedrightleft[i];	
			}else{
				decryptleft[leftIndex] = decryptedrightleft[i];
				leftIndex++;
			}	
		}
		
		char[][] deleft = new char[17][32];
		char[][] deright = new char[17][32];
		deleft[16] = decryptleft;//l16
		deright[16] = decryptright;//r16
		for (int i=deleft.length-2;i>=0 ;i-- ) {
			deright[i] = deleft[i+1];
			deleft[i] = getRow(deright,deleft,i+1,subKeys,i+1,true);
		}
		char[] decryptedIP = new char[64];
		int j=0;
		for (int i=0;i<decryptedIP.length ;i++ ) {
			if (i<32) {
				decryptedIP[i]=deleft[0][i];	
			}else{
				decryptedIP[i]=deright[0][j];
				j++;
			}

		}
		char[] decryptedMessage = new char[64];
		int decryptedIPIndex=0;
		for (int i=0;i<ipArray.length ;i++ ) {
    	 	for (int x=0;x<ipArray[i].length ;x++ ) {
    	 		decryptedMessage[ipArray[i][x]-1] = decryptedIP[decryptedIPIndex];
    	 		decryptedIPIndex++;			
			}					
		}		
		String hexDecryptedMessage = getHexMessage(decryptedMessage) ;
		return hexDecryptedMessage;
	}

    public char[] getRow(char[][] l,char[][] r,int row,char[][] subKeys,int subKeyIndex,boolean flow){
    	int j;
        int[] e = {32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1};
    	int[][] eTable = new int[8][6];
    	int	eIndex = 0;
    	for (int i=0;i<eTable.length ;i++ ) {
    		for (j=0;j<eTable[i].length ;j++ ) {
    			eTable[i][j] = e[eIndex];
    			eIndex++;
    		}
    	}

    	char[] eR0 = new char[48];
    	int eR0Index = 0;
    	for (int i=0;i<eTable.length ;i++ ) {
    		for (j=0;j<eTable[i].length ;j++ ) {
    			eR0[eR0Index] = r[row][eTable[i][j]-1];
    			eR0Index++;
    		}
    	}

    	int[][] subKeyER = new int[8][6];
    	int	exORIndex = 0;
    	for (int i=0;i<subKeyER.length ;i++ ) {
    		for (j=0;j<subKeyER[i].length ;j++ ) {
    			subKeyER[i][j] = subKeys[subKeyIndex][exORIndex]^eR0[exORIndex];
    			exORIndex++;
    		}
    	}

    	int[][] s1 ={
    				{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
    				{0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
    				{4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
    				{15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13} 
    				};

    	int[][] s2 ={
    				{15,1,8,14,6,1,3,4,9,7,2,13,12,0,5,10},
     			    {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
      				{0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
 					{13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}
    				};

   		int[][] s3 ={
   					{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
     				{13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
     				{13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
      				{1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}
   					};

   		int[][] s4 ={
   					{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
   					{13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
   					{10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
   					{3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}
			   		};
		int[][] s5 ={
					{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
				    {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
					{4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
					{11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}
					};
		int[][] s6 ={
					{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
					{10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
					{9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
					{4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}
					};
		int[][] s7 ={
					{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
					{13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
					{1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
					{6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}
					};		
		int[][] s8 ={
					{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
					{1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
					{7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
					{2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}
					};
		int[][][] sImp = new int[8][4][15]; 							    					 				
    	sImp[0] = s1;
    	sImp[1] = s2;
    	sImp[2] = s3;
    	sImp[3] = s4;
    	sImp[4] = s5;
    	sImp[5] = s6;
    	sImp[6] = s7;
    	sImp[7] = s8;

    	int[][] fourBitArray = new int[8][4];			
    	for (int i=0;i<subKeyER.length ;i++ ) {
    		fourBitArray[i] = sixTofourBit(sImp[i],subKeyER[i]); 
    	}

    	int[] totalSbox = new int[32];
    	int totalSboxIndex = 0;
    	for (int i=0;i<fourBitArray.length ;i++ ) {
    		for (j=0;j<fourBitArray[i].length ;j++ ) {
    			totalSbox[totalSboxIndex] =	fourBitArray[i][j];
    			totalSboxIndex++;
    		}
    	}

    	int[][] permutation = {
    						  {16,7,20,21},
    						  {29,12,28,17},
    						  {1,15,23,26},
    						  {5,18,31,10},
    						  {2,8,24,14},
    						  {32,27,3,9},
    						  {19,13,30,6},
    						  {22,11,4,25}
    						  };

    	 
    	int	rIndex = 0;
    	String str = "";
    	for (int i=0;i<permutation.length ;i++ ) {
    	 	for (j=0;j<permutation[i].length ;j++ ) {
    	 		str+=totalSbox[permutation[i][j]-1];				  		
		  	}			  	
	    }
	    char[] r1 = str.toCharArray();
	    if (flow) {
	    	int[] finalr1 = new int[32];
		    for (int i=0;i<finalr1.length ;i++ ) {
		    	finalr1[i] = l[row][i]^r1[i]; 
		    }

	        String strResult = "";
	        for (int i=0;i<finalr1.length ;i++ ) {
	            strResult+=finalr1[i];
	        }
	        char[] result = strResult.toCharArray();
	        return result;
	    }else{
	    	return r1;
	    }
    }

    public String getHexMessage(char[] encryptedBits){
    	String hexencryptedBits = "";
		String hexencryption = "";
		int bitIndex=0;
		for (int i=0;i<encryptedBits.length ;i++ ) {
			hexencryption+=encryptedBits[i];
			
			if (hexencryption.length()==4) {
				hexencryptedBits += Integer.toHexString(Integer.parseInt(hexencryption,2)).toUpperCase();
				hexencryption = "";
			}
		}
		return hexencryptedBits;
    }


    public static void main(String[] args)
    		throws Exception
    {
    	DES des = new DES();

        String plainText = "0000000100100011010001010110011110001001101010111100110111101111";
	    String keyString = "0001001100110100010101110111100110011011101111001101111111110001";
		char[] key = keyString.toCharArray();

		System.out.println("Enter Text Message:");
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String dummy = br.readLine();
		
		byte[] text = dummy.getBytes();
		String message = "";
		for (int i=0;i<text.length ;i++ ) {
			message+=Integer.toHexString(text[i]);
		}
		for (int i=0;i<message.length()%16 ;i++ ) {
			message+="0";
		}

		String messagebits = "";
		for (int i=0;i<message.length() ;i++ ) {
		messagebits += String.format("%4s",Integer.toBinaryString(Integer.parseInt(message.substring(i,i+1),16))).replace(' ','0');
		}

		String[] textMessage = new String[messagebits.length()/64];
		for (int i=0;i<textMessage.length ;i++ ) {
			textMessage[i] = messagebits.substring(i*64,(i+1)*64);
		}

    	int[] PC1 = {57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,37,29,21,13,5,28,20,12,4};
    	
    	
    	int[][] keyArray = new int[8][7];
    	int pc1=0;
    	for (int i=0;i<keyArray.length ;i++ ) {
    		for (int j=0;j<keyArray[i].length ;j++ ) {
    			keyArray[i][j] = PC1[pc1];
    			pc1++;
    		}
    	}

    	char[] keyPlus = des.getKeyPlus(keyArray,key);
    	
		char[] c0 = new char[28];
		char[] d0 = new char[28];

		int j=0;
		int k=0;
		for (int i=0;i<keyPlus.length ;i++ ) {
			if (i<28) {
				c0[j] = keyPlus[i];
				j++;
			}else{
				d0[k] = keyPlus[i];
				k++;
			}
		}

		int[] iterationNumber = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};
		
		char[][] c = new char[17][28];
		c[0] = c0;
		for (int i=1;i<c.length ;i++ ) {
			c[i] = des.getShiftedArray(iterationNumber[i-1],c[i-1]);
		}
		char[][] d = new char[17][28];
		d[0] = d0;
		for (int i=1;i<d.length ;i++ ) {
			d[i] = des.getShiftedArray(iterationNumber[i-1],d[i-1]);
		}
		int[] PC2 = {14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32};

		int[][] subKeyArray = new int[8][6];
    	int pc2=0;
    	for (int i=0;i<subKeyArray.length ;i++ ) {
    		for (int x=0;x<subKeyArray[i].length ;x++ ) {
    			subKeyArray[i][x] = PC2[pc2];
    			pc2++;
    		}
    	}

    	char[][] subKeys = new char[17][56];
    	subKeys[0] = keyPlus;
    	for (int i=1;i<subKeys.length ;i++ ) {
    		subKeys[i] = des.getSubKey(c[i],d[i],subKeyArray); 
    	}

    	int[] initialPermutation = {58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6,64,56,48,40,32,24,16,8,57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7};

    	int[][] ipArray = new int[8][8];
    	int ipIndex = 0;
    	for (int i=0;i<ipArray.length ;i++ ) {
    		for (j=0;j<ipArray[i].length ;j++ ) {
    			ipArray[i][j] = initialPermutation[ipIndex];
    			ipIndex++;	
    		}
    	}
    	
    	int[][] ipInverse = {
    						{40,8,48,16,56,24,64,32},
    						{39,7,47,15,55,23,63,31},
    						{38,6,46,14,54,22,62,30},
    						{37,5,45,13,53,21,61,29},
    						{36,4,44,12,52,20,60,28},
    						{35,3,43,11,51,19,59,27},
    						{34,2,42,10,50,18,58,26},
    						{33,1,41,9,49,17,57,25}
    						};
    	String hexEncryptedMessage = "";
    	for (int i=0;i<textMessage.length ;i++ ) {
 			hexEncryptedMessage += des.encryption(ipArray,subKeys,ipInverse,textMessage[i]);
    	} 
		System.out.println(hexEncryptedMessage);
		/*Decryption Begins*/
		/*Add the string got from encryption here and replace that with hexencryptedbits below
		for checking*/

		System.out.println("Please enter the encrypted text to get the message");
		hexEncryptedMessage = br.readLine();	
		String hexDecryptedMessage = "";
		for (int i=0;i<hexEncryptedMessage.length()/16 ;i++ ) {
	        hexDecryptedMessage += des.decryption(ipArray,subKeys,ipInverse,hexEncryptedMessage.substring(i*16,(i+1)*16));	
		}	
		
		byte[] textDecrypt = new byte[hexDecryptedMessage.length()/2];
	    j=0;
		for (int i=0;i<textDecrypt.length ;i++ ) {
		 	textDecrypt[i] = Byte.parseByte(hexDecryptedMessage.substring(j,j+2),16);
		 	j+=2;
		} 
		System.out.println(new String(textDecrypt));
    }       
}


                                