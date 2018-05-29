#include <openssl/bio.h>
#include <openssl/applink.c>
#include <openssl/bn.h>
#include <string.h>

struct Entry_in_Dictionary
{
	BIGNUM* key;
	BIGNUM* value;
};

const int b = 16;
const int b0 = 8;
const int size_of_dictionary = 255;
const int message = 43151;
const int length_of_p = 64;

//**************************************************************************************************************************************************
//*                                               ElGamal Encryption System                                                                        *
//**************************************************************************************************************************************************
void Improved_Key_Generation_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key, int bit_num);
void Flawed_Key_Generation_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key, int bit_num);
void Encryption_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, 
						BIGNUM* cipertext1, BIGNUM* cipertext2, BIGNUM* plaintext);
void Decryption_ElGamal(BIGNUM* cipertext1, BIGNUM* cipertext2, BIGNUM* p_prime_public_key, BIGNUM* x_private_key, BIGNUM* RecoveredText);

//**************************************************************************************************************************************************
//*                                                   Attacking Scheme                                                                             *
//**************************************************************************************************************************************************
int  compare(const void*a, const void*b); // used for qsort and bsearch
void Meet_in_the_Middle_Attacking_Scheme_ElGamal(BIGNUM* p, BIGNUM* v, BIGNUM* plaintext);
void Build_up_Dictionary_Attacking_Scheme_ElGamal(Entry_in_Dictionary* Dictionary, BIGNUM* p);
void Search_Dictionary_Attacking_Scheme_ElGamal(Entry_in_Dictionary* Dictionary, BIGNUM* p, BIGNUM* v, BIGNUM* plaintext);

//**************************************************************************************************************************************************
//*                                                 For Test Purpose Only                                                                          *
//**************************************************************************************************************************************************
void Key_Assignment_TestOnly(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key);
void Plaintext_Assignment_TestOnly(BIGNUM* plaintext);
void Ciphertext_Assignment_TestOnly(BIGNUM* cipertext1, BIGNUM* cipertext2);

//**************************************************************************************************************************************************
//*                                                    IO Functions                                                                                *
//**************************************************************************************************************************************************
void Print_BIGNUM_dec(BIGNUM* number);

//**************************************************************************************************************************************************
//*                                                   Main Function                                                                                *
//**************************************************************************************************************************************************
int main()
{
	BIGNUM* p_prime_public_key;
	BIGNUM* g_public_key;
	BIGNUM* y_public_key;
	BIGNUM* x_private_key;
	BIGNUM* cipertext1;
	BIGNUM* cipertext2;
	BIGNUM* plaintext;
	BIGNUM* RecoveredText;
	p_prime_public_key = BN_new();
	g_public_key = BN_new();
	y_public_key = BN_new();
	x_private_key = BN_new();
	cipertext1 = BN_new();
	cipertext2 = BN_new();
	plaintext = BN_new();
	RecoveredText = BN_new();

	// Key_Assignment_TestOnly(p_prime_public_key, g_public_key, y_public_key, x_private_key);
	// Improved_Key_Generation_ElGamal(p_prime_public_key, g_public_key, y_public_key, x_private_key, length_of_p);
	Flawed_Key_Generation_ElGamal(p_prime_public_key, g_public_key, y_public_key, x_private_key, length_of_p);
    Plaintext_Assignment_TestOnly(plaintext);
	printf("Public Key:\n");
	printf("p = ");
	Print_BIGNUM_dec(p_prime_public_key);
	printf("g = ");
	Print_BIGNUM_dec(g_public_key);
	printf("y = g^x = ");
	Print_BIGNUM_dec(y_public_key);
	printf("\n");
	printf("Private Key:\n");
	printf("x = ");
	Print_BIGNUM_dec(x_private_key);
	printf("\n");
	printf("Plaintext: ");
	Print_BIGNUM_dec(plaintext);
	Encryption_ElGamal(p_prime_public_key, g_public_key, y_public_key, 
						cipertext1, cipertext2, plaintext);
	printf("\n");
	printf("Ciphertext: \n");
	Print_BIGNUM_dec(cipertext1);
	Print_BIGNUM_dec(cipertext2);
	/* One test case with assigned cipertext (For test purpose only)
	Ciphertext_Assignment_TestOnly(cipertext1, cipertext2);
	printf("\n");
	printf("Ciphertext: \n");
	Print_BIGNUM_dec(cipertext1);
	Print_BIGNUM_dec(cipertext2);
    */
    Decryption_ElGamal(cipertext1, cipertext2, p_prime_public_key, x_private_key, RecoveredText);
	printf("\n");
	printf("Recovered text: ");
	Print_BIGNUM_dec(RecoveredText);

    Meet_in_the_Middle_Attacking_Scheme_ElGamal(p_prime_public_key, cipertext2, plaintext);

	BN_free(p_prime_public_key);
	BN_free(g_public_key);
	BN_free(y_public_key);
	BN_free(x_private_key);
	BN_free(cipertext1);
	BN_free(cipertext2);
	BN_free(plaintext);
	BN_free(RecoveredText);
	system("pause");
	return 0;
}

void Improved_Key_Generation_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key, int bit_num)
{
	// Generate random safe prime p and get q=(p-1)/2
	BN_CTX* ctx;
	BIGNUM* temp;
	BIGNUM* q_prime;
	BIGNUM* b;
	BN_ULONG w = 1;
	int safe = 1;
	ctx = BN_CTX_new();
	b = BN_new();

	q_prime = BN_new();
	temp = BN_new();
	BN_generate_prime(p_prime_public_key, bit_num, safe, NULL,
                              NULL, NULL, NULL);
	BN_copy(q_prime, p_prime_public_key);
	BN_sub_word(q_prime, w);
	w = 2;
	BN_div_word(q_prime, w);

    // generate a random genarator(primitive root) g for cyclic Group G
	while(1)
	{
		BN_pseudo_rand_range(g_public_key, p_prime_public_key);
		BN_mod_sqr(b, g_public_key, p_prime_public_key, ctx);
		if (BN_is_one(b))
			continue;
		BN_mod_exp(b, g_public_key, q_prime, p_prime_public_key, ctx);
		if (BN_is_one(b))
			continue;
		break;
    }
    
	// generate y = g^x as one of the public keys; x is the private key
    w = 2;
	BN_copy(temp, p_prime_public_key);
	BN_sub_word(temp, w);
	BN_pseudo_rand_range(x_private_key, temp);
	BN_mod_exp(y_public_key, g_public_key, x_private_key, p_prime_public_key, ctx);

    BN_free(q_prime);
	BN_free(temp);
	BN_free(b);
}

// g is not primitive and we suppose n=|g|=q=(p-1)/2
void Flawed_Key_Generation_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key, int bit_num)
{
	// Generate random safe prime p and get q=(p-1)/2
	BN_CTX* ctx;
	BIGNUM* temp;
	BIGNUM* q_prime;
	BIGNUM* b;
	BN_ULONG w = 1;
	int safe = 1;
	ctx = BN_CTX_new();
	b = BN_new();

	q_prime = BN_new();
	temp = BN_new();
	BN_generate_prime(p_prime_public_key, bit_num, safe, NULL,
                              NULL, NULL, NULL);
	BN_copy(q_prime, p_prime_public_key);
	BN_sub_word(q_prime, w);
	w = 2;
	BN_div_word(q_prime, w);

    // generate a random g in cyclic Group G, g is not primitive and |g||q
	while(1)
	{
		BN_pseudo_rand_range(g_public_key, p_prime_public_key);
		BN_mod_sqr(b, g_public_key, p_prime_public_key, ctx);
		if (BN_is_one(b))
			continue;
		BN_mod_exp(b, g_public_key, q_prime, p_prime_public_key, ctx);
		if (BN_is_one(b))
			break;
    }
    
	// generate y = g^x as one of the public keys; x is the private key
    w = 2;
	BN_copy(temp, p_prime_public_key);
	BN_sub_word(temp, w);
	BN_pseudo_rand_range(x_private_key, temp);
	BN_mod_exp(y_public_key, g_public_key, x_private_key, p_prime_public_key, ctx);

    BN_free(q_prime);
	BN_free(temp);
	BN_free(b);
}

void Encryption_ElGamal(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, 
						BIGNUM* cipertext1, BIGNUM* cipertext2, BIGNUM* plaintext)
{
	BIGNUM* k;
	BIGNUM* temp;
	BN_CTX* ctx;
	BN_ULONG w = 2;
	k = BN_new();
    temp = BN_new();
	ctx = BN_CTX_new();
	BN_copy(temp, p_prime_public_key);
	BN_sub_word(temp, w);
	BN_pseudo_rand_range(k, temp);

    BN_mod_exp(cipertext1, g_public_key, k, p_prime_public_key, ctx);
	BN_mod_exp(cipertext2, y_public_key, k, p_prime_public_key, ctx);
	BN_mod_mul(cipertext2, cipertext2, plaintext, p_prime_public_key, ctx);

	BN_free(k);
	BN_free(temp);
}

void Decryption_ElGamal(BIGNUM* cipertext1, BIGNUM* cipertext2, BIGNUM* p_prime_public_key, BIGNUM* x_private_key, BIGNUM* RecoveredText)
{
	BN_CTX* ctx;
	ctx = BN_CTX_new();
	BN_mod_exp(RecoveredText, cipertext1, x_private_key, p_prime_public_key, ctx);
	BN_mod_inverse(RecoveredText, RecoveredText, p_prime_public_key, ctx);
	BN_mod_mul(RecoveredText, RecoveredText, cipertext2, p_prime_public_key, ctx);
}

void Key_Assignment_TestOnly(BIGNUM* p_prime_public_key, BIGNUM* g_public_key, BIGNUM* y_public_key, BIGNUM* x_private_key)
{
	unsigned long p = 2357;
	unsigned long alpha = 2;
	unsigned long x = 1185;
	unsigned long a = 1751;
	BN_set_word(p_prime_public_key, p);
	BN_set_word(g_public_key, alpha);
	BN_set_word(y_public_key, x);
	BN_set_word(x_private_key, a);
}

void Plaintext_Assignment_TestOnly(BIGNUM* plaintext)
{
	unsigned long text = message;
	BN_set_word(plaintext, text);
}

void Ciphertext_Assignment_TestOnly(BIGNUM* cipertext1, BIGNUM* cipertext2)
{
	unsigned long c1 = 1430;
	unsigned long c2 = 697;
	BN_set_word(cipertext1, c1);
	BN_set_word(cipertext2, c2);
}

void Print_BIGNUM_dec(BIGNUM* number)
{
	char* p = NULL;
    p = BN_bn2dec(number);
    printf("%s\n", p); 
    OPENSSL_free(p);
}

void Meet_in_the_Middle_Attacking_Scheme_ElGamal(BIGNUM* p, BIGNUM* v, BIGNUM* plaintext)
{
	// Define the Dictionary
	struct Entry_in_Dictionary* Dictionary = new Entry_in_Dictionary[size_of_dictionary];
	Build_up_Dictionary_Attacking_Scheme_ElGamal(Dictionary, p);
	Search_Dictionary_Attacking_Scheme_ElGamal(Dictionary, p, v, plaintext);
}

void Build_up_Dictionary_Attacking_Scheme_ElGamal(Entry_in_Dictionary* Dictionary, BIGNUM* p)
{
	int i;
	BIGNUM* n;
	BN_CTX* ctx;
    ctx = BN_CTX_new();
	BN_ULONG w;
    BIGNUM *m1;
    m1 = BN_new();

	// Compute n=(p-1)/2
	BN_ULONG r = 1;
	n = BN_new();
	BN_copy(n, p);
	BN_sub_word(n, r);
	r = 2;
	BN_div_word(n, r);

	// Build up the Dictionary
	for (i = 1; i <= size_of_dictionary; i++)
	{
		w = i;
		BN_zero(m1);
        BN_add_word(m1, w);

		Dictionary[i-1].key = BN_new();
        Dictionary[i-1].value = BN_new();
        BN_copy(Dictionary[i-1].value, m1);
        BN_mod_exp(Dictionary[i-1].key, m1, n, p, ctx);
	}

	BN_free(n);
	BN_free(m1);
	qsort(Dictionary, size_of_dictionary, sizeof(Entry_in_Dictionary), compare);
	printf("Dictionary for Attacking Scheme has been built up successfully!\n");
	system("pause");
}

// Used by qsort and bsearch
int compare(const void*a, const void*b)
{
	struct Entry_in_Dictionary* p = (Entry_in_Dictionary*) a;
	struct Entry_in_Dictionary* q = (Entry_in_Dictionary*) b;
	return BN_cmp((*p).key, (*q).key);
}

void Search_Dictionary_Attacking_Scheme_ElGamal(Entry_in_Dictionary* Dictionary, BIGNUM* p, BIGNUM* v, BIGNUM* plaintext)
{
	int i;
	int flag = 0; // flag marks the result of the search: flag == 1 means success; flag == 0 means failure
	int number_of_m_guessed = 0;
	int number_of_m_correct = 0;
	BIGNUM* n;
	BIGNUM* v_n;
	BIGNUM* m_cracked;
	BIGNUM* search_target_value; // search_target_value contains v^n * m2^(-n)
	Entry_in_Dictionary* search_result = NULL;
	Entry_in_Dictionary* search_target = new Entry_in_Dictionary;
	(*search_target).key = BN_new();
	BN_CTX* ctx;
    ctx = BN_CTX_new();
	BN_ULONG w;
    BIGNUM *m2;
    m2 = BN_new();
	v_n = BN_new();
	m_cracked = BN_new();
	search_target_value = BN_new();

	// Compute n = (p-1)/2
	BN_ULONG r = 1;
	n = BN_new();
	BN_copy(n, p);
	BN_sub_word(n, r);
	r = 2;
	BN_div_word(n, r);

	// Compute v_n = v^n
    BN_mod_exp(v_n, v, n, p, ctx);

	// Try to find m = m1*m2
	// Assume that b2=b1=b/2
	for (i = 1; i <= size_of_dictionary; i++)
	{
		w = i;
		BN_zero(m2);
        BN_add_word(m2, w);
		BN_mod_exp(search_target_value, m2, n, p, ctx);
		BN_mod_inverse(search_target_value, search_target_value, p, ctx);
		BN_mod_mul(search_target_value, search_target_value, v_n, p, ctx);
		BN_copy((*search_target).key, search_target_value);
		
		// Use binary search bsearch to find the solution
		search_result = (Entry_in_Dictionary*)bsearch(search_target, Dictionary, size_of_dictionary, 
			             sizeof(Entry_in_Dictionary), compare);
		if (search_result != NULL)
		{
			flag = 1;
		    number_of_m_guessed++;
			// printf("Great, our attacking scheme has cracked this message!\n");
		    // Store the cracked message m = m1*m2 in m_cracked.
            BN_mod_mul(m_cracked, (*search_result).value, m2, p, ctx);
			
			if (BN_cmp(m_cracked, plaintext) == 0)
				number_of_m_correct++;
			// printf("The cracked text message is: \n");
            // Print_BIGNUM_dec(m_cracked);
			search_result = NULL;
			// break;
		}
	}
    
	if (flag == 0)
		printf("Sorry, our attacking scheme cannot crack this message!\n");
	
	if (flag == 1)
	{
		// Store the cracked message m = m1*m2 in m_cracked.
        // BN_mod_mul(m_cracked, (*search_result).value, m2, p, ctx);
		// printf("The number of possible text message is: %d\n", number_of_m_guessed);
		printf("The number of correct text message is: %d\n", number_of_m_correct);
	}
	    
	BN_free(n);
	BN_free(m2);
	BN_free(v_n);
	BN_free(search_target_value);
	BN_free(m_cracked);
}
