#include<iostream>
using namespace std;

int IP[64] = {58,50,42,34,26,18,10,2,
              60,52,44,36,28,20,12,4,
			  62,54,46,38,30,22,14,6,
			  64,56,48,40,32,24,16,8,
			  57,49,41,33,25,17, 9,1,
			  59,51,43,35,27,19,11,3,
			  61,53,45,37,29,21,13,5,
			  63,55,47,39,31,23,15,7};

int IP_R[64] = {40,8,48,16,56,24,64,32,
                39,7,47,15,55,23,63,31,
				38,6,46,14,54,22,62,30,
				37,5,45,13,53,21,61,29,
				36,4,44,12,52,20,60,28,
				35,3,43,11,51,19,59,27,
				34,2,42,10,50,18,58,26,
				33,1,41, 9,49,17,57,25};

int E[48] = {32,1, 2, 3, 4, 5, 
             4, 5, 6, 7, 8, 9, 
			 8, 9, 10,11,12,13,
			 12,13,14,15,16,17,
			 16,17,18,19,20,21,
			 20,21,22,23,24,25,
			 24,25,26,27,28,29,
			 28,29,30,31,32, 1};

int S_Functions[8][4][16] = {14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7,
                             0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8,
							 4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0,
							 15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13,
							 
							 15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10,
							 3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5,
							 0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15,
							 13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9,
							 
							 10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8,
							 13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1,
							 13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7,
							 1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12,

							 7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15,
							 13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9,
							 10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4,
							 3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14,

							 2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9,
							 14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6,
							 4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14,
							 11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3,

							 12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11,
							 10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8,
							 9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6,
							 4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13,
							 
							 4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1,
							 13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6,
							 1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2,
							 6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12, 

							 13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7,
							 1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2,
							 7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8,
							 2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11};

int P[32] = {16,7 ,20,21,
             29,12,28,17,
			 1 ,15,23,26,
			 5 ,18,31,10,
			 2 ,8,24, 14,
			 32,27, 3, 9,
			 19,13, 30,6,
			 22,11, 4, 25}; 

int PC_1[56] = {57,49,41,33,25,17,9,
                1,58,50,42,34,26,18,
				10,2,59,51,43,35,27,
				19,11,3,60,52,44,36,
				63,55,47,39,31,23,15,
				7,62,54,46,38,30,22,
				14,6,61,53,45,37,29,
				21,13,5,28,20,12, 4};

int PC_2[48] = {14,17,11,24,1,5,
                3,28,15,6,21,10,
				23,19,12,4,26,8,
				16,7,27,20,13,2,
				41,52,31,37,47,55,
				30,40,51,45,33,48,
				44,49,39,56,34,53,
				46,42,50,36,29,32};

int LS[16] = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};

void DES(bool* text, bool* key);
void Rotation_Function(bool* text, bool* key);
void DES_Decode(bool* text, bool* key);
void Rotation_Function_Decode(bool* text, bool* key);
void BinaryEncode(bool* text, char* inputText);
void BinaryDecode(bool* text, char* outputText);

bool Keys[16][48];

int main()
{
    char* plainText;
	char* encodedText;
	char* decodedText;
	char* Key;
	bool* tempText;
	bool* key;
	plainText = new char[8];
    encodedText = new char[8];
	decodedText = new char[8];
	Key = new char[8];
    tempText = new bool[64];
	key = new bool[64];

	int choose;
	cin >> choose;
	while ((choose != 1)&&(choose != 2))
	{
		cin >> choose;
	}

	if (choose == 1)
	{
		int start;
		cin >> start;
		Key[0] = 'S'; Key[1] = 'E'; Key[2] = 'C'; Key[3] = 'U';
		Key[4] = 'R'; Key[5] = 'I'; Key[6] = 'T'; Key[7] = 'Y';
		if (start == 1)
		{
			int i;
			plainText[0] = 'N'; plainText[1] = 'E'; plainText[2] = 'T'; plainText[3] = 'W';
		    plainText[4] = 'O'; plainText[5] = 'R'; plainText[6] = 'K'; plainText[7] = ' ';

	        BinaryEncode(tempText, plainText);
	        BinaryEncode(key, Key);	
	        DES(tempText, key);
            BinaryDecode(tempText, encodedText);

	        for (i = 0; i < 8; i++)
                cout << encodedText[i];
	        cout << endl;
            
            DES_Decode(tempText, key);
	        BinaryDecode(tempText, decodedText);
	        for (i = 0; i < 8; i++)
                cout << decodedText[i];
	        cout << endl;
			cout << endl;

			plainText[0] = 'I'; plainText[1] = 'N'; plainText[2] = 'F'; plainText[3] = 'O';
		    plainText[4] = 'R'; plainText[5] = 'M'; plainText[6] = 'A'; plainText[7] = 'T';

	        BinaryEncode(tempText, plainText);
	        BinaryEncode(key, Key);	
	        DES(tempText, key);
            BinaryDecode(tempText, encodedText);
	
	        for (i = 0; i < 8; i++)
                cout << encodedText[i];
	        cout << endl;

            DES_Decode(tempText, key);
	        BinaryDecode(tempText, decodedText);
	        for (i = 0; i < 8; i++)
                cout << decodedText[i];
	        cout << endl;
			cout << endl;

			plainText[0] = 'I'; plainText[1] = 'O'; plainText[2] = 'N'; plainText[3] = ' ';
		    plainText[4] = 'S'; plainText[5] = 'E'; plainText[6] = 'C'; plainText[7] = 'U';
            
	        BinaryEncode(tempText, plainText);
	        BinaryEncode(key, Key);	
	        DES(tempText, key);
            BinaryDecode(tempText, encodedText);
	
	        for (i = 0; i < 8; i++)
                cout << encodedText[i];
	        cout << endl;

            DES_Decode(tempText, key);
	        BinaryDecode(tempText, decodedText);
	        for (i = 0; i < 8; i++)
                cout << decodedText[i];
	        cout << endl;
			cout << endl;

			plainText[0] = 'R'; plainText[1] = 'I'; plainText[2] = 'T'; plainText[3] = 'Y';
		    plainText[4] = ' '; plainText[5] = ' '; plainText[6] = ' '; plainText[7] = ' ';

	        BinaryEncode(tempText, plainText);
	        BinaryEncode(key, Key);	
	        DES(tempText, key);
            BinaryDecode(tempText, encodedText);

	        for (i = 0; i < 8; i++)
                cout << encodedText[i];
	        cout << endl;

            DES_Decode(tempText, key);
	        BinaryDecode(tempText, decodedText);
	        for (i = 0; i < 8; i++)
                cout << decodedText[i];
	        cout << endl;  
		}
	}

	if (choose == 2)
	{
        int i;
	    for (i = 0; i < 8; i++)
            cin >> plainText[i];
	
  	    for (i = 0; i < 8; i++)
            cin >> Key[i];
 
	    BinaryEncode(tempText, plainText);
	    BinaryEncode(key, Key);	
	    DES(tempText, key);
        BinaryDecode(tempText, encodedText);
	
	    for (i = 0; i < 8; i++)
            cout << encodedText[i];
	    cout << endl;

        DES_Decode(tempText, key);
	    BinaryDecode(tempText, decodedText);
	    for (i = 0; i < 8; i++)
            cout << decodedText[i];
	    cout << endl;
	}

	delete[] plainText;
	delete[] encodedText;
	delete[] decodedText;
	delete[] Key;
	delete[] tempText;
	delete[] key;
	
	return 0;
}

void DES(bool* text, bool* key)
{
	bool text_copy[64];
	int i;
	for (i = 0; i < 64; i++)
	{
		text_copy[i] = text[i];
	}

	for (i = 0; i < 64; i++)
	{
		text[i] = text_copy[IP[i]-1];
	}

	Rotation_Function(text, key);

	for (i = 0; i < 32; i++)
    {
		bool temp;
		temp = text[i];
		text[i] = text[32+i];
		text[32+i] = temp;
	}

    for (i = 0; i < 64; i++)
	{
		text_copy[i] = text[i];
	}

    for (i = 0; i < 64; i++)
	{
		text[i] = text_copy[IP_R[i]-1];
	}

}

void Rotation_Function(bool* text, bool* key)
{
	bool L0[32], R0[32];
	bool L[32], R[32];
	int j;
	for (j = 0; j < 32; j++)
		L0[j] = text[j];
	for (j = 32; j < 64; j++)
		R0[j-32] = text[j];

	bool C[28], D[28];
    for (j = 0; j < 28; j++)
		C[j] = key[PC_1[j]-1];
	for (j = 28; j < 56; j++)
		D[j-28] = key[PC_1[j]-1];
    


	int number = 1;
	while(number <= 16)
	{
		int k;
		bool Extension[48];
		bool K[48];
		for (k = 0; k < 32; k++)
			L[k] = R0[k];

		for (k = 0; k < 48; k++)
		{
			Extension[k] = R0[E[k]-1];
		}

		bool tempa, tempb, tempc, tempd;
		if (LS[number-1] == 1)
		{
			tempa = C[0];
			tempc = D[0];
            for (k = 0; k < 27; k++)
			{
				C[k] = C[k+1];
				D[k] = D[k+1];
			}
			C[27] = tempa;
			D[27] = tempc;
		}
		else
		{
            tempa = C[0];
			tempb = C[1];
			tempc = D[0];
			tempd = D[1];
            for (k = 0; k < 26; k++)
			{
				C[k] = C[k+1];
				D[k] = D[k+1];
			}
			C[26] = tempa;
			C[27] = tempb;
			D[26] = tempc;
			D[27] = tempd;
		}
         
        bool CD[56];
		for (k = 0; k < 28; k++)
			CD[k] = C[k];
	    for (k = 28; k < 56; k++)
			CD[k] = D[k-28];

		for (k = 0; k < 48; k++)
		{
			K[k] = CD[PC_2[k]-1];
   		    Keys[number-1][k] = K[k];
		}
		
		for(k = 0; k < 48; k++)
			Extension[k] = Extension[k]^K[k];

		bool S[32];
		bool currentGroup[6];
		int groupNumber;
		for (groupNumber = 0; groupNumber < 8; groupNumber++)
		{
			int start1 = groupNumber*6;
			int start2 = groupNumber*4;
			int t;
			for (t = 0; t < 6; t++)
				currentGroup[t] = Extension[start1+t];
			
			int col, row;
			int outputNumber;
			bool result[4];
			row = currentGroup[0]*2+currentGroup[5];
			col = currentGroup[1]*8+currentGroup[2]*4+currentGroup[3]*2+currentGroup[4];
            outputNumber = S_Functions[groupNumber][row][col];
            result[3] = outputNumber%2;
			outputNumber /= 2;
			result[2] = outputNumber%2;
			outputNumber /= 2;
			result[1] = outputNumber%2;
			outputNumber /= 2;
			result[0] = outputNumber%2;

			for(t = 0; t < 4; t++)
				S[start2+t] = result[t];
		}
        
		bool P_Result[32];
		for (k = 0; k < 32; k++)
			P_Result[k] = S[P[k]-1];

		for (k = 0; k < 32; k++)
			R[k] = L0[k]^P_Result[k];
        
		bool* tempResult;
		char* tempOutput;
		tempResult = new bool[64];
		tempOutput = new char[8];
		for (j = 0; j < 32; j++)
		    tempResult[j] = L[j];
	    for (j = 32; j < 64; j++)
		    tempResult[j] = R[j-32];
		
		BinaryDecode(tempResult, tempOutput);
		cout << " 第" << number << "轮迭代的中间结果是：";
		for (j = 0; j < 8; j++)
			cout << tempOutput[j];
		cout << endl;

        delete[] tempResult;
		delete[] tempOutput;

		for (k = 0; k < 32; k++)
		{
			L0[k] = L[k];
			R0[k] = R[k];
		}
		number++;
	}

	for (j = 0; j < 32; j++)
		text[j] = L[j];
	for (j = 32; j < 64; j++)
		text[j] = R[j-32];
}

void BinaryEncode(bool* text, char* inputText)
{
	int start;
	int Text;
	int i;
	for (i = 0; i < 8; i++)
	{
		start = i*8;
		Text = (int)(inputText[i]);
		int j;
		for (j = 0; j < 8; j++)
		{
			text[start+7-j] = Text%2;
			Text /= 2;
		}
	}
}

void BinaryDecode(bool* text, char* outputText)
{
	int start;
	int i;
	for (i = 0; i < 8; i++)
	{
		int Text = 0;
		start = i*8;
		Text = text[start]*128+text[start+1]*64+text[start+2]*32+text[start+3]*16+text[start+4]*8
			  +text[start+5]*4+text[start+6]*2+text[start+7];
		outputText[i] = (char)Text;
	}
}

void DES_Decode(bool* text, bool* key)
{
	bool text_copy[64];
	int i;
	for (i = 0; i < 64; i++)
	{
		text_copy[i] = text[i];
	}

	for (i = 0; i < 64; i++)
	{
		text[i] = text_copy[IP[i]-1];
	}

	Rotation_Function_Decode(text, key);

	for (i = 0; i < 32; i++)
    {
		bool temp;
		temp = text[i];
		text[i] = text[32+i];
		text[32+i] = temp;
	}

    for (i = 0; i < 64; i++)
	{
		text_copy[i] = text[i];
	}

    for (i = 0; i < 64; i++)
	{
		text[i] = text_copy[IP_R[i]-1];
	}

}

void Rotation_Function_Decode(bool* text, bool* key)
{
	bool L0[32], R0[32];
	bool L[32], R[32];
	int j;
	for (j = 0; j < 32; j++)
		L0[j] = text[j];
	for (j = 32; j < 64; j++)
		R0[j-32] = text[j];

	bool C[28], D[28];
    for (j = 0; j < 28; j++)
		C[j] = key[PC_1[j]-1];
	for (j = 28; j < 56; j++)
		D[j-28] = key[PC_1[j]-1];
    


	int number = 1;
	while(number <= 16)
	{
		int k;
		bool Extension[48];
		bool K[48];
		for (k = 0; k < 32; k++)
			L[k] = R0[k];

		for (k = 0; k < 48; k++)
		{
			Extension[k] = R0[E[k]-1];
		}

		for (k = 0; k < 48; k++)
		{
			K[k] = Keys[16-number][k];
   		}

		for(k = 0; k < 48; k++)
			Extension[k] = Extension[k]^K[k];

		bool S[32];
		bool currentGroup[6];
		int groupNumber;
		for (groupNumber = 0; groupNumber < 8; groupNumber++)
		{
			int start1 = groupNumber*6;
			int start2 = groupNumber*4;
			int t;
			for (t = 0; t < 6; t++)
				currentGroup[t] = Extension[start1+t];
			
			int col, row;
			int outputNumber;
			bool result[4];
			row = currentGroup[0]*2+currentGroup[5];
			col = currentGroup[1]*8+currentGroup[2]*4+currentGroup[3]*2+currentGroup[4];
            outputNumber = S_Functions[groupNumber][row][col];
            result[3] = outputNumber%2;
			outputNumber /= 2;
			result[2] = outputNumber%2;
			outputNumber /= 2;
			result[1] = outputNumber%2;
			outputNumber /= 2;
			result[0] = outputNumber%2;

			for(t = 0; t < 4; t++)
				S[start2+t] = result[t];
		}

		bool P_Result[32];
		for (k = 0; k < 32; k++)
			P_Result[k] = S[P[k]-1];

		for (k = 0; k < 32; k++)
			R[k] = L0[k]^P_Result[k];
        
		for (k = 0; k < 32; k++)
		{
			L0[k] = L[k];
			R0[k] = R[k];
		}
		number++;
	}

	for (j = 0; j < 32; j++)
		text[j] = L[j];
	for (j = 32; j < 64; j++)
		text[j] = R[j-32];
}