/* 
Author: Luke Pederson
Title: Megaman
Abstract: Thesis processes data for the wonderfetch spreadsheet for Internet Archives
Date: June 9, 2011
*/

#include <iostream>
#include <fstream>
#include <string>

using namespace std;

//Skips first three lines then grabs Headers
void skip_first_lines(&ifstream in_file,&ofstream out_file,&string header[MAX],int number_of_cols);

//See below for details
void generate_url(&ifstream in_file,&ofstream out_file,,&header[MAX],int number_of_cols,int number_of_cust);

//Max array size, used for number of columns
const MAX=100;

int main (){
	
	//File Prep
	ifstream in_file;
	in_file.open("wonderfetch.txt");
	
	if (in_file.fail()){
		cout << "Error opening input file" << endl;
		return -1;
	}

	ofstream out_file;
	out_file.open("wonderfetch_fixed.txt");
	
	if (out_file.fail()){
		cout << "Error opening output file" << endl;
		return -1;
	}
	
	//Declare array for headers
	string header[MAX];
	int number_of_cols=0;
	//Skip and grab headers call
	skip_first_lines(in_file,out_file,header[MAX],number_of_cols);
	
	int number_of_cust=0;
	
	cout << "How many Custom Fields are there?: ";
	cin >> number_of_cust;
	
	
	while(!in_file.eof()){
	
		generate_url(in_file,out_file,header[MAX],number_of_cols,number_of_cust);

	}
	
	return 0;
}

//This function gets a line from the input file. It gets a character at time and adds it to a string.
//If the char is a \tab it set that array cell to being empty. Function stops when it hits end of line.

void generate_url(&ifstream in_file,&ofstream out_file,&header[MAX],int number_of_cols,int number_of_cols,){
	
	//Full string
	string full_string;
	
	//Incremented string for each cell
	string tab_string;

	
	char letter='a';
	string cell[MAX];
	
	
	
	getline(in_file,full_string);
	
	if(full_string!=0){
		
		while (letter!='\n'){
			
			while (letter!='\t'){
				
				letter=getchar(in_file);
				
				if(letter!='\n'){
				
					if(letter=='\t'){
					
						cell[i]='';
						
					} else {
					
					tab_string=tab_string+letter;
					
					}
				
					cell[i]=tab_string;
				}
			}
		}
	}
	
	cell[number_of_cust-3]="http://yaz.us.archive.org/biblio.php?f=c&z_e=" + cell [29] + "&z_c=" = cell[30] + "&z_d=" + cell[5] + "&b_l=" + cell[25] + "&b_c1=" + cell[26] "&b_c2=" + cell[27] + "&b_n=" + cell[28] + "&b_p=" + cell[24] + "&b_v=" + cell[7] + "&pcs=" + cell[16] + "&lic=" + cell[19] + "&rights=" + cell[20] + "&dd=" + cell[21] + "&b_rtl=" + cell[15] + "&b_plt=" + cell[31] + "&w_wonderfetch=1&w_title=" + cell[6] + "&w_subject=" + cell[12] + "&w_publisher=" + cell[13] + "&w_date=" + cell[9] + "&w_language=" + cell[11] + "&w_creator=" + cell[8] + "&w_description=" + cell[14] + "&b_c3=" + cell[1] + "&b_cl=" + cell[32] + "&b_sf=" + cell[33] + "&b_id=" + cell [4]" ;

int k=1;	
for(int i=num_of_cust-26;i<num_of_cust;i++){
	if(cell[i]!=''){
		cell[number_of_cols-3]=cell[number_of_cols-3] + "&c[" + k + "]=" + header[i] + "&v[" + k + "]=" + cell [i];
		k++;
	}
}

if(cell[10]!=''){
	cell[number_of_cols-3]=cell[number_of_cols-3] + "&c[0]=call_number&v[0]=" + cell[10];
}
if(cell[number_of_cols-9]!=''){
	cell[number_of_cols-3]=cell[number_of_cols-3] + "&stype=m&c[k+3]=notes&v[" + (k+3) + "]=Film/Fiche is presented as originally captured."
}
if(cell[number_of_cols-8]!=''){
	cell[number_of_cols-3]=cell[number_of_cols-3] + "&b_skip_r=true";
}
if(cell[number_of_cols-7]!=''){
	cell[number_of_cols-3]=cell[number_of_cols-3] + "&c[" + (k+1) + "]=item_number&v[" + (k+1) + "]=" + cell[51];
}
if(cell[number_of_cols-6]!=''){
	cell[number_of_cols-3]=cell[number_of_cols-3] + "&c[" + (k+2) + "]=preset&v[" + (k+2) + "]=" + cell[52];
}

//Original Code from wonderfetch
/*
="http://yaz.us.archive.org/biblio.php?f=c&z_e="&AD5&"&z_c="&AE5&"&z_d="&F5&"&b_l="&Z5&"&b_c1="&AA5&"&b_c2="&AB5&"&b_n="&AC5&"&b_p="&Y5&"&b_v="&H5&"&pcs="&Q5&"&lic="&T5&"&rights="&U5&"&dd="&V5&"&b_rtl="&P5&"&b_plt="&AF5&"&w_wonderfetch=1&w_title="&G5&"&w_subject="&M5&"&w_publisher="&N5&"&w_date="&J5&"&w_language="&L5&"&w_creator="&I5&"&w_description="&O5&"&b_c3="&B5&"&b_cl="&AG5&"&b_sf="&AH5&"&b_id="&E5&IF(AI5="","","&c[1]="&AI$3&"&v[1]="&AI5)&IF(AJ5="","","&c[2]="&AJ$3&"&v[2]="&AJ5)&IF(Ak5="","","&c[3]="&Ak$3&"&v[3]="&Ak5)&IF(Al5="","","&c[4]="&Al$3&"&v[4]="&Al5)&IF(Am5="","","&c[5]="&Am$3&"&v[5]="&Am5)&IF(An5="","","&c[6]="&An$3&"&v[6]="&An5)&IF(Ao5="","","&c[7]="&Ao$3&"&v[7]="&Ao5)&IF(Ap5="","","&c[8]="&Ap$3&"&v[8]="&Ap5)&IF(Aq5="","","&c[9]="&Aq$3&"&v[9]="&Aq5)&IF(Ar5="","","&c[10]="&Ar$3&"&v[10]="&Ar5)&IF(As5="","","&c[11]="&As$3&"&v[11]="&As5)&IF(At5="","","&c[12]="&At$3&"&v[12]="&At5)&IF(Au5="","","&c[13]="&Au$3&"&v[13]="&Au5)&IF(Av5="","","&c[14]="&Av$3&"&v[14]="&Av5)&IF(AW5="","","&c[15]="&AW$3&"&v[15]="&AW5)&IF(K5="","","&c[0]=call_number&v[0]="&K5)&IF(AX5="","","&stype=m&c[6]=notes&v[6]=Film/Fiche is presented as originally captured.")&IF(AY5="","","&b_skip_r=true")&IF(AZ5="","","&c[4]=item_number&v[4]="&AZ5)&IF(BA5="","","&c[5]=preset&v[5]="&BA5)
*/

}

void skip_first_lines(&ifstream in_file,&ofstream out_file,&string header[MAX],int number_of_cols){
	string s1;
	char letter='a';
	string header[MAX];
	number_of_cols=0;
	
	for(int i=0;i<3;i++){
		getline(in_file,s1);
		out_file << s1;
		}
	getline(in_file,s1);

while (letter!='\n'){
			
			while (letter!='\t'){
				
				letter=getchar(in_file);
				
				if(letter!='\n'){
				
					if(letter=='\t'){
					
						header[i]='';
						
					} else {
					
					tab_string=tab_string+letter;
					
					}
				
					header[i]=tab_string;
				}
			}
			number_of_cols++;
		}	
}