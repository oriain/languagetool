package org.languagetool.rules.it;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.languagetool.JLanguageTool;
import org.languagetool.TestSentence;
import org.languagetool.TestTools;
import org.languagetool.language.Italian;
import org.languagetool.rules.RuleMatch;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by littl on 10/14/2016.
 */
public class AgreementTest {
    private static JLanguageTool lt;
    private static ResourceBundle ItalianResourceBundle;
    private AgreementRule rule;
    private boolean enableAssertions = true;

    public static List<TestSentence> testSentences = Arrays.asList(
        new TestSentence("test1_VPPS_AGREE_incorrect",			"I have explained the lesson and she has followed very carefully.",					"Ho spiegato la lezione e lei ha seguita molto attentamente.",						1),
        new TestSentence("test2_VPPS_AGREE_correct",			"I have explained the lesson and she has followed very carefully.",					"Ho spiegato la lezione e lei ha seguito molto attentamente.",						0),
        new TestSentence("test3_VPPS_AGREE_incorrect",			"My sons have emigrated.",															"I miei figli sono emigrato all'estero.",											1),
        new TestSentence("test4_VPPS_AGREE_correct",			"My sons have emigrated.",															"I miei figli sono emigrati all'estero.",											0),
        new TestSentence("test5_VPPS_AGREE_incorrect",			"That car cost me an arm and a leg (lit. an eye from my head).",					"Quella macchina mi è costato un occhio della testa.",								1),
        new TestSentence("test6_VPPS_AGREE_correct",			"That car cost me an arm and a leg (lit. an eye from my head).",					"Quella macchina mi è costata un occhio della testa.",								0),
        new TestSentence("test7_VPPS_AGREE_incorrect",			"St. Peter's square had seemed to her an enchanted kingdom.",						"Piazza San Pietro a lei era sembrato un regno incantato.",							1),
        new TestSentence("test8_VPPS_AGREE_correct",			"St. Peter's square had seemed to her an enchanted kingdom.",						"Piazza San Pietro a lui era sembrata un regno incantato.",							0),
        new TestSentence("test9_VPPS_AGREE_incorrect",			"The twin girls were born on May 6.",												"Le gemelle erano nato il 6 maggio.",												1),
        new TestSentence("test10_VPPS_AGREE_correct",			"The twin girls were born on May 6.",												"Le gemelle erano nate il 6 maggio.",												0),
        new TestSentence("test11_VPPS_AGREE_incorrect",			"Dragons have never existed outside fairy tales.",									"Non sono mai esistito draghi fuori dalle favole.",									1),
        new TestSentence("test12_VPPS_AGREE_correct",			"Dragons have never existed outside fairy tales.",									"Non sono mai esistiti draghi fuori dalle favole.",									0),
        new TestSentence("test13_VPPS_AGREE_incorrect",			"We liked your poems (lit. Your poems were pleasing to us.) ",						"Ci sono piaciuto le tue poesie.",													1),
        new TestSentence("test14_VPPS_AGREE_correct",			"We liked your poems (lit. Your poems were pleasing to us.)",						"Ci sono piaciute le tue poesie.",													0),
        new TestSentence("test15_VPPS_AGREE_incorrect",			"The bricklayers started at 9 and finished at 3.",									"I muratori hanno cominciati alle otto e finito alle tre.",							1),
        new TestSentence("test16_VPPS_AGREE_correct",			"The bricklayers started at 9 and finished at 3.",									"I muratori hanno cominciato alle otto e finito alle tre.",							0),
        new TestSentence("test17_VPPS_AGREE_incorrect",			"You had to go away.",																"Ve ne siete dovuto andare.",														1),
        new TestSentence("test18_VPPS_AGREE_correct",			"You had to go away.",																"Ve ne siete dovuti andare.",														0),
        new TestSentence("test19_VPPS_AGREE_incorrect",			"You had to go away.",																"Avete dovuti andarvene.",															1),
        new TestSentence("test20_VPPS_AGREE_correct",			"You had to go away.",																"Avete dovuto andarvene.",															0),
        new TestSentence("test21_VPPS_AGREE_incorrect",			"All these things were able to be sold at a good price.",							"Tutte queste cose hanno potute essere vendute a buon prezzo.",						1),
        new TestSentence("test22_VPPS_AGREE_correct",			"All these things were able to be sold at a good price.",							"Tutte queste cose hanno potuto essere vendute a buon prezzo.",						0),
        new TestSentence("test23_VPPS_AGREE_incorrect",			"When did you get up, girls? ",														"A che ora vi siete alzato, ragazze?",												1),
        new TestSentence("test24_VPPS_AGREE_correct",			"When did you get up, girls?",														"A che ora vi siete alzate, ragazze?",												0),
        new TestSentence("test25_VPPS_AGREE_incorrect",			"Maria bought the skirt.",															"Maria ha comprata la gonna.",														1),
        new TestSentence("test26_VPPS_AGREE_correct",			"Maria bought the skirt.",															"Maria ha comprato la gonna.",														0),
        new TestSentence("test27_VPPS_AGREE_incorrect",			"The apples were bought by Luisa.",													"Le mele sono comprato da Luisa.",													1),
        new TestSentence("test28_VPPS_AGREE_correct",			"The apples were bought by Luisa.",													"Le mele sono comprate da Luisa.",													0),
        new TestSentence("test29_VPPS_AGREE_incorrect",			"As soon as were seated in the room, the film began. (lit. the film was started) ",	"Appena ci siamo seduto in sala, è iniziato il film.",								1),
        new TestSentence("test30_VPPS_AGREE_correct",			"As soon as were seated in the room, the film began. (lit. the film was started)",	"Appena ci siamo seduti in sala, è iniziato il film.",								0),
        new TestSentence("test31_VPPDOP_AGREE_incorrect",		"Afterward she had given them to him.",												"E dopo, gliele avrebbe dato.",														1),
        new TestSentence("test32_VPPDOP_AGREE_correct",			"Afterward she had given them to him.",												"E dopo, gliele avrebbe date.",														0),
        new TestSentence("test33_VPPS_AGREE_incorrect",			"Paul has cut his hair.",															"Paolo si è tagliato i capelli.",													1),
        new TestSentence("test34_VPPS_AGREE_correct",			"Paul has cut his hair.",															"Paolo si è tagliati i capelli.",													0),
        new TestSentence("test35_VPPDOP_AGREE_incorrect",		"Paul knew that two new discs had come out and he bought them.",					"Paolo ha saputo che sono usciti due nuovi dischi e li ha comprato.",				1),
        new TestSentence("test36_VPPDOP_AGREE_correct",			"Paul knew that two new discs had come out and he bought them.",					"Paolo ha saputo che sono usciti due nuovi dischi e li ha comprati.",				0),
        new TestSentence("test37_VPPDOP_AGREE_incorrect",		"Yes, I have cooked them.",															"Si, le ho cucinato.",																1),
        new TestSentence("test38_VPPDOP_AGREE_correct",			"Yes, I have cooked them.",															"Si, le ho cucinate.",																0),
        new TestSentence("test39_VPPDOP_AGREE_incorrect",		"No, we did not listen to it.",														"No, non le abbiamo ascoltato.",													1),
        new TestSentence("test40_VPPDOP_AGREE_correct",			"No, we did not listen to it.",														"No, non le abbiamo ascoltate.",													0),
        new TestSentence("test41_VPPDOP_AGREE_incorrect",		"We met them.",																		"Le abbiamo incontrato.",															1),
        new TestSentence("test42_VPPDOP_AGREE_correct",			"We met them.",																		"Le abbiamo incontrate.",															0),
        new TestSentence("test1_ART_AGREE_incorrect",			"We never eat meat.",																"Non mangiamo mai il carne.",														1),
        new TestSentence("test2_ART_AGREE_correct",				"We never eat meat.",																"Non mangiamo mai la carne.",														0),
        new TestSentence("test3_ART_AGREE_incorrect",			"Money is the root of all evil.",													"Denaro è il fonte di tutti i mali.",												1),
        new TestSentence("test4_ART_AGREE_correct",				"Money is the root of all evil.",													"Il denaro è la fonte di tutti i mali.",											0),
        new TestSentence("test5_ART_AGREE_incorrect",			"Should I take the medicine before meals or after meals? ",							"Devo prendere la medicina prima di pasti o dopo pasti?",							1),
        new TestSentence("test6_ART_AGREE_correct",				"Should I take the medicine before meals or after meals?",							"Devo prendere la medicina prima dei pasti o dopo i pasti?",						0),
        new TestSentence("test7_ART_AGREE_incorrect",			"Do you know the story of Pinocchio? ",												"Conosci il storia di Pinocchio?",													1),
        new TestSentence("test8_ART_AGREE_correct",				"Do you know the story of Pinocchio?",												"Conosci la storia di Pinocchio?",													0),
        new TestSentence("test9_ART_FORM_incorrect",			"I don't like sports at all.",														"Il sport non mi piace per niente.",												1),
        new TestSentence("test10_ART_FORM_correct",				"I don't like sports at all.",														"Lo sport non mi piace per niente.",												0),
        new TestSentence("test11_ART_FORM_incorrect",			"Italians are nice.",																"I Italiani sono simpatici.",														1),
        new TestSentence("test12_ART_FORM_correct",				"Italians are nice.",																"Gli Italiani sono simpatici.",														0),
        new TestSentence("test13_ART_FORM_incorrect",			"The Greek islands are famous.",													"Gli isole greche sono famose.",													1),
        new TestSentence("test14_ART_FORM_correct",				"The Greek islands are famous.",													"Le isole greche sono famose.",														0),
        new TestSentence("test15_ART_FORM_incorrect",			"What are the colors of the Italian flag? ",										"Quali sono le colori della bandiera italiana?",									1),
        new TestSentence("test16_ART_FORM_correct",				"What are the colors of the Italian flag?",											"Quali sono i colori della bandiera italiana?",										0),
        new TestSentence("test17_ART_FORM_incorrect",			"The nice [upscale] stores have lovely windows.",									"I bei negozi hanno i belle vetrine.",												1),
        new TestSentence("test18_ART_FORM_correct",				"The nice [upscale] stores have lovely windows.",									"I bei negozi hanno le belle vetrine.",												0),
        new TestSentence("test19_ART_FORM_incorrect",			"Tomorrow is my only opportunity to leave work early.",								"Domani è la unica occasione che posso lasciare lavoro in anticipo.",				1),
        new TestSentence("test20_ART_FORM_correct",				"Tomorrow is my only opportunity to leave work early.",								"Domani è l'unica occasione che posso lasciare il lavoro in anticipo.",				0),
        new TestSentence("test21_ART_REQUIRED_incorrect",		"I have to get a driver's license.",												"Devo prendere una patente.",														1),
        new TestSentence("test22_ART_REQUIRED_correct",			"I have to get a driver's license.",												"Devo prendere la patente.",														0),
        new TestSentence("test23_ART_REQUIRED_incorrect",		"There are automobiles even on the sidewalks! ",									"Ci sono macchine anche su marciapiedi!",											1),
        new TestSentence("test24_ART_REQUIRED_correct",			"There are automobiles even on the sidewalks!",										"Ci sono macchine anche sui marciapiedi!",											0),
        new TestSentence("test25_ART_REQUIRED_incorrect",		"He stayed in bed because he had a cough.",											"Era rimasto a letto perchè aveva una tosse.",										1),
        new TestSentence("test26_ART_REQUIRED_correct",			"He stayed in bed because he had a cough.",											"Era rimasto a letto perchè aveva la tosse.",										0),
        new TestSentence("test27_ART_REQUIRED_incorrect",		"Men kid themselves that they can change the course of history.",					"Uomini si illudono di dare un corso diverso a storia.",							1),
        new TestSentence("test28_ART_REQUIRED_correct",			"Men kid themselves that they can change the course of history.",					"Gli uomini si illudono di dare un corso diverso alla storia.",						0),
        new TestSentence("test29_ART_REQUIRED_incorrect",		"Wine is bad for your health.",														"Vino fa male a salute.",															1),
        new TestSentence("test30_ART_REQUIRED_correct",			"Wine is bad for your health.",														"Il vino fa male alla salute.",														0),
        new TestSentence("test31_ART_REQUIRED_incorrect",		"Hatred is perhaps the most powerful of human passions.",							"Odio è forse la più potente di passioni umane.",									1),
        new TestSentence("test32_ART_REQUIRED_correct",			"Hatred is perhaps the most powerful of human passions.",							"L'odio è forse la più potente delle passioni umane.",								0),
        new TestSentence("test33_ART_REQUIRED_incorrect",		"My father is a carpenter.",														"Mio padre fa un falegname.",														1),
        new TestSentence("test34_ART_REQUIRED_correct",			"My father is a carpenter.",														"Mio padre fa il falegname.",														0),
        new TestSentence("test35_ART_REQUIRED_incorrect",		"Don't be an idiot.",																"Non fare uno scemo.",																1),
        new TestSentence("test36_ART_REQUIRED_correct"	,		"Don't be an idiot.",																"Non fare lo scemo.",																0),
        new TestSentence("test37_ART_REQUIRED_incorrect",		"His back hurts.",																	"Gli fa male la sua schiena.",														1),
        new TestSentence("test38_ART_REQUIRED_correct",			"His back hurts.",																	"Gli fa male la schiena.",															0),
        new TestSentence("test39_ART_REQUIRED_incorrect",		"Eigty percent of the students in my Italian class are girls.",						"Ottanta per cento degli studenti nella mia classe d'Italiano sono ragazze.",		1),
        new TestSentence("test40_ART_REQUIRED_correct",			"Eigty percent of the students in my Italian class are girls.",						"L'ottanta per cento degli studenti nella mia classe d'Italiano sono ragazze.",		0),
        new TestSentence("test41_ART_TITLE_incorrect",			"I'm sorry but Dr. Zambetti hasn't arrived yet.",									"Mi dispiace ma dottor Zambetti non è ancora arrivato.",							1),
        new TestSentence("test42_ART_TITLE_correct",			"I'm sorry but Dr. Zambetti hasn't arrived yet.",									"Mi dispiace ma il dottor Zambetti non è ancora arrivato.",							0),
        new TestSentence("test43_ART_TITLE_incorrect",			"Good morning, Dr. Zambetti.",														"Buon giorno, il dottor Zambetti.",													1),
        new TestSentence("test44_ART_TITLE_correct",			"Good morning, Dr. Zambetti.",														"Buon giorno, dottor Zambetti.",													0),
        new TestSentence("test45_ART_TITLE_incorrect",			"My teacher goes to Italy every year.",												"Mio professore va in Italia tutti gli anni.",										1),
        new TestSentence("test46_ART_TITLE_correct",			"My teacher goes to Italy every year.",												"Il mio professore va in Italia tutti gli anni.",									0),
        new TestSentence("test47_ART_TUTTO_incorrect",			"We have already seen both movies.",												"Abbiamo già visto tutti e due film.",												1),
        new TestSentence("test48_ART_TUTTO_correct",			"We have already seen both movies.",												"Abbiamo già visto tutti e due i film.",											0),
        new TestSentence("test49_ART_TUTTO_incorrect",			"All his relatives live in the south of Italy.",									"Tutti suoi parenti abitano nel sud d'Italia.",										1),
        new TestSentence("test50_ART_TUTTO_correct",			"All his relatives live in the south of Italy.",									"Tutti i suoi parenti abitano nel sud d'Italia.",									0),
        new TestSentence("test51_ART_TUTTO_incorrect",			"He likes to spend all summer with his grandmother.",								"Gli piace trascorrere tutta estate con la nonna.",									1),
        new TestSentence("test52_ART_TUTTO_correct",			"He likes to spend all summer with his grandmother.",								"Gli piace trascorrere tutta l'estate con la nonna.",								0),
        new TestSentence("test53_ART_TUTTO_incorrect",			"My teacher goes to Italy every year.",												"Il mio professore va in Italia tutti anni.",										1),
        new TestSentence("test54_ART_TUTTO_correct",			"My teacher goes to Italy every year.",												"Il mio professore va in Italia tutti gli anni.",									0),
        new TestSentence("test55_ART_BODY_incorrect",			"One must recognize his own mistakes.",												"Bisogna riconoscere suoi propri errori.",											1),
        new TestSentence("test56_ART_BODY_correct",				"One must recognize his own mistakes.",												"Bisogna riconoscere i propri errori.",												0),
        new TestSentence("test57_ART_BODY_incorrect",			"Every pupil must raise his hand to ask to go out.",								"Ciascuno alunno deve alzare sua mano per chiedere di uscire.",						1),
        new TestSentence("test58_ART_BODY_correct",				"Every pupil must raise his hand to ask to go out.",								"Ciascuno alunno deve alzare la mano per chiedere di uscire.",						0),
        new TestSentence("test59_ART_BODY_incorrect",			"The cat has scratched your face.",													"Il gatto ha graffiato la tua faccia.",												1),
        new TestSentence("test60_ART_BODY_correct",				"The cat has scratched your face.",													"Il gatto ti ha graffiato la faccia.",												0),
        new TestSentence("test61_ART_BODY_incorrect",			"Maria paints her nails.",															"Maria si dipinge sue unghie.",														1),
        new TestSentence("test62_ART_BODY_correct",				"Maria paints her nails.",															"Maria si dipinge le unghie.",														0),
        new TestSentence("test63_ART_BODY_incorrect",			"My teeth hurt.",																	"I miei denti mi dolgono.",															1),
        new TestSentence("test64_ART_BODY_correct",				"My teeth hurt.",																	"Mi dolgono i denti.",																0),
        new TestSentence("test65_ART_BODY_incorrect",			"Paul raised his head from (his) books.",											"Paolo alzò la sua testa dai libri.",												1),
        new TestSentence("test66_ART_BODY_correct",				"Paul raised his head from (his) books.",											"Paolo alzò la testa dai libri.",													0),
        new TestSentence("test67_ART_PROPER_incorrect",			"Have you met Alfredo? ",															"Hai incontrato l'Alfredo?",														1),
        new TestSentence("test68_ART_PROPER_correct",			"Have you met Alfredo?",															"Hai incontrato Alfredo?",															0),
        new TestSentence("test69_ART_PROPER_incorrect",			"My favorite city is Venice.",														"La mia città preferita è la Venezia.",												1),
        new TestSentence("test70_ART_PROPER_correct",			"My favorite city is Venice.",														"La mia città preferita è Venezia.",												0),
        new TestSentence("test71_ART_PROPER_incorrect",			"The Rome of my youth was very different.",											"Roma della mia gioventù era molto diversa.",										1),
        new TestSentence("test72_ART_PROPER_correct",			"The Rome of my youth was very different.",											"La Roma della mia gioventù era molto diversa.",									0),
        new TestSentence("test73_ART_PROPER_incorrect",			"Do you remember poor Antonio? ",													"Ti ricordi povero Antonio?",														1),
        new TestSentence("test74_ART_PROPER_correct",			"Do you remember poor Antonio?",													"Ti ricordi il povero Antonio?",													0),
        new TestSentence("test75_ART_PROPER_incorrect",			"Trastevere is a very typical district of Rome.",									"Il Trastevere è un quartiere molto caratteristico della Roma.",					1),
        new TestSentence("test76_ART_PROPER_correct",			"Trastevere is a very typical district of Rome.",									"Trastevere è un quartiere molto caratteristico di Roma.",							0),
        new TestSentence("test77_ART_PROPER_incorrect",			"Via Condotti is the luxury shoppping street in Rome.",								"La via Condotti è la via dello shopping di lusso alla Roma.",						1),
        new TestSentence("test78_ART_PROPER_correct",			"Via Condotti is the luxury shoppping street in Rome.",								"Via Condotti è la via dello shopping di lusso a Roma.",							0),
        new TestSentence("test79_ART_PROPER_incorrect",			"Tokyo is a very large city.",														"Il Tokyo è una città molto grande.",												1),
        new TestSentence("test80_ART_PROPER_correct",			"Tokyo is a very large city.",														"Tokyo è una città molto grande.",													0),
        new TestSentence("test81_ART_PROPER_incorrect",			"Tuscany is the region I know best.",												"Per me, Toscana è la ragione che conosco meglio.",									1),
        new TestSentence("test82_ART_PROPER_correct",			"Tuscany is the region I know best.",												"Per me, la Toscana è la ragione che conosco meglio.",								0),
        new TestSentence("test85_ART_APPOSITIVE_incorrect",		"Your friend has been elected chairman of the meeting.",							"Il tuo amico è stato eletto il presidente dell'assemblea.",						1),
        new TestSentence("test86_ART_APPOSITIVE_correct",		"Your friend has been elected chairman of the meeting.",							"Il tuo amico è stato eletto presidente dell'assemblea.",							0),
        new TestSentence("test87_ART_APPOSITIVE_incorrect",		"He called me a thief.",															"Mi ha chiamato un ladro.",															1),
        new TestSentence("test88_ART_APPOSITIVE_correct",		"He called me a thief.",															"Mi ha chiamato ladro.",															0),
        new TestSentence("test89_ART_APPOSITIVE_incorrect",		"Rome, capital of Italy, is rich in history.",										"Roma, la capitale d'Italia, è ricca di storia.",									1),
        new TestSentence("test90_ART_APPOSITIVE_correct",		"Rome, capital of Italy, is rich in history.",										"Roma, capitale d'Italia, è ricca di storia.",										0),
        new TestSentence("test91_ART_TIME_incorrect",			"From four to seven I work in the garden.",											"Da quattro a sette lavoro in giardino.",											1),
        new TestSentence("test92_ART_TIME_correct",				"From four to seven I work in the garden.",											"Dalle quattro alle sette lavoro in giardino.",										0),
        new TestSentence("test93_ART_TIME_incorrect",			"On Tuesdays and Fridays the signora goes to town.",								"Martedì e venerdì la signora va in città.",										1),
        new TestSentence("test94_ART_TIME_correct",				"On Tuesdays and Fridays the signora goes to town.",								"Il martedì e il venerdì la signora va in città.",									0),
        new TestSentence("test95_ART_TIME_incorrect",			"Next Tuesday, she will vist her daughter instead.",								"Il martedì prossimo, invece, visiterà sua figlia.",								1),
        new TestSentence("test96_ART_TIME_correct",				"Next Tuesday, she will vist her daughter instead.",								"Martedì prossimo, invece, visiterà la figlia.",									0),
        new TestSentence("test97_ART_TIME_incorrect",			"It is eleven o'clock.",															"Sono undici.",																		1),
        new TestSentence("test98_ART_TIME_correct",				"It is eleven o'clock.",															"Sono le undici.",																	0),
        new TestSentence("test99_ART_TIME_incorrect",			"The express for Torino departs at ten thirty from track twelve.",					"Il rapido per Torino parte a dieci e trenta dal binario dodici.",					1),
        new TestSentence("test100_ART_TIME_correct",			"The express for Torino departs at ten thirty from track twelve.",					"Il rapido per Torino parte alle dieci e trenta dal binario dodici.",				0),
        new TestSentence("test101_ART_TIME_incorrect",			"January is usually a very cold month in Italy.",									"Il gennaio è un mese di solito molto freddo in Italia.",							1),
        new TestSentence("test102_ART_TIME_correct",			"January is usually a very cold month in Italy.",									"Gennaio è un mese di solito molto freddo in Italia.",								0),
        new TestSentence("test103_ART_KIN_incorrect",			"Do your aunts live here? ",														"Abitano qui tue zie?",																1),
        new TestSentence("test104_ART_KIN_correct",				"Do your aunts live here?",															"Abitano qui le tue zie?",															0),
        new TestSentence("test105_ART_KIN_incorrect",			"Claudio looks for his white shirt in his sister's room.",							"Claudio cerca la sua camicia bianca nella stanza della sua sorella.",				1),
        new TestSentence("test106_ART_KIN_correct",				"Claudio looks for his white shirt in his sister's room.",							"Claudio cerca la sua camicia bianca nella stanza di sua sorella.",					0),
        new TestSentence("test107_ART_KIN_incorrect",			"How young your mom is! ",															"Com'è giovane tua mamma.",															1),
        new TestSentence("test108_ART_KIN_correct",				"How young your mom is!",															"Com'è giovane la tua mamma.",														0),
        new TestSentence("test109_ART_KIN_incorrect",			"Their father works abroad.",														"Loro padre lavora all'estero.",													1),
        new TestSentence("test110_ART_KIN_correct",				"Their father works abroad.",														"Il loro padre lavora all'estero.",													0),
        new TestSentence("test111_ART_KIN_incorrect",			"Your son really loves his sister.",												"Vostro figlio ama davvero la sua sorella.",										1),
        new TestSentence("test112_ART_KIN_correct",				"Your son really loves his sister.",												"Il vostro figlio ama davvero sua sorella.",										0),
        new TestSentence("test113_ART_KIN_incorrect",			"We sons love our dear mother.",													"Noi figli amiamo nostra cara mamma.",												1),
        new TestSentence("test114_ART_KIN_correct",				"We sons love our dear mother.",													"Noi figli amiamo la nostra cara mamma.",											0),
        new TestSentence("test115_ART_KIN_incorrect",			"I have never met (I never knew) my cousin from Naples.",							"Non ho mai conosciuto mio cugino di Napoli.",										1),
        new TestSentence("test116_ART_KIN_correct",				"I have never met (I never knew) my cousin from Naples.",							"Non ho mai conosciuto il mio cugino di Napoli.",									0),
        new TestSentence("test117_ART_KIN_incorrect",			"What mother doesn't love her children? ",											"Quale madre non ama suoi figli?",													1),
        new TestSentence("test118_ART_KIN_correct",				"What mother doesn't love her children?",											"Quale madre non ama i suoi figli?",												0),
        new TestSentence("test119_ART_KIN_incorrect",			"This is my book. (This book is mine).",											"Questo libro è il mio.",															1),
        new TestSentence("test120_ART_KIN_correct",				"This is my book. (This book is mine).",											"Questo libro è mio.",																0),
        new TestSentence("test121_ART_KIN_incorrect",			"His wife is German, did you know? ",												"La sua moglie è tedesca, lo sapevi?",												1),
        new TestSentence("test122_ART_KIN_correct",				"His wife is German, did you know?",												"Sua moglie è tedesca, lo sapevi?",													0),
        new TestSentence("test123_ART_KIN_incorrect",			"What is your (little) brother's name? ",											"Allora, come si chiama tuo fratellino?",											1),
        new TestSentence("test124_ART_KIN_correct",				"What is your (little) brother's name?",											"Allora, come si chiama il tuo fratellino?",										0),
        new TestSentence("test135_ART_PAIR_LIST_incorrect",		"The airplane flew over cities, lakes, mountains, seas, and islands.",				":L'aeroplano sorvolò le città, i laghi, i monti, i mari e le isole.",				1),
        new TestSentence("test136_ART_PAIR_LIST_correct",		"The airplane flew over cities, lakes, mountains, seas, and islands.",				":L'aeroplano sorvolò città, laghi, monti, mari e isole.",							0),
        new TestSentence("test137_ART_PAIR_LIST_incorrect",		"Could I have a little (some) bread and cheese? ",									"Potrei avere un po' della pane e del formaggio?",									1),
        new TestSentence("test138_ART_PAIR_LIST_correct",		"Could I have a little (some) bread and cheese?",									"Potrei avere un po' di pane e formaggio?",											0),
        new TestSentence("test139_ART_PAIR_LIST_incorrect",		"I have brought the passport, the tickets, the itinerary, and the cellphone.",		"Ho portato il passaporto, i biglietti, l'agenda, il cellulare.",					1),
        new TestSentence("test140_ART_PAIR_LIST_correct",		"I have brought the passport, the tickets, the itinerary, and the cellphone.",		"Ho portato passaporto, biglietti, agenda, cellulare.",								0),
        new TestSentence("test143_ART_W_PREP_incorrect",		"They reacted calmly.",																"Hanno reagito con la calma.",														1),
        new TestSentence("test144_ART_W_PREP_correct",			"They reacted calmly.",																"Hanno reagito con calma.",															0),
        new TestSentence("test145_ART_W_PREP_incorrect",		"Tomorrow morning I'm not going to school, I'm staying home.",						"Domani mattina non vado alla scuola, rimango a casa.",								1),
        new TestSentence("test146_ART_W_PREP_correct",			"Tomorrow morning I'm not going to school, I'm staying home.",						"Domani mattina non vado a scuola, rimango a casa.",								0),
        new TestSentence("test147_ART_SUPERL_ADJ_incorrect",	"It's the longest movie I have [ever] seen.",										"È il film il più lungo che io abbia visto.",										1),
        new TestSentence("test148_ART_SUPERL_ADJ_correct",		"It's the longest movie I have [ever] seen.",										"È il film più lungo che io abbia visto.",											0),
        new TestSentence("test149_ART_SUPERL_ADJ_incorrect",	"The most intelligent boy in the family never went to college.",					"Il ragazzo il più intelligente della famiglia non è mai andato all'università.",	1),
        new TestSentence("test150_ART_SUPERL_ADJ_correct",		"The most intelligent boy in the family never went to college.",					"Il ragazzo più intelligente della famiglia non è mai andato all'università.",		0),
        new TestSentence("test151_ART_SUPERL_ADV_incorrect",	"He spoke the most rapidly of all (of anyone).",									"Ha parlato più il rapidamente di tutti.",											1),
        new TestSentence("test152_ART_SUPERL_ADV_correct",		"He spoke the most rapidly of all (of anyone).",									"Ha parlato più rapidamente di tutti.",												0),
        new TestSentence("test153_ART_SUPERL_ADV_incorrect",	"I returned home as soon as possible.",												"Sono toranto a casa più presto possibile.",										1),
        new TestSentence("test154_ART_SUPERL_ADV_correct",		"I returned home as soon as possible.",												"Sono tornato a casa il più presto possibile.",										0),
        new TestSentence("test155_ART_OMITTED_incorrect",		"I don't read any newspapers.",														"Non leggo i giornali.",															1),
        new TestSentence("test156_ART_OMITTED_correct",			"I don't read any newspapers.",														"Non leggo giornali.",																0),
        new TestSentence("test157_ART_OMITTED_incorrect",		"Our dog is always hungry.",														"Il nostro cane sempre ha la fame.",												1),
        new TestSentence("test158_ART_OMITTED_correct",			"Our dog is always hungry.",														"Il nostro cane sempre ha fame.",													0)
    );

    @BeforeClass
    public static void toolSetup() {
        ItalianResourceBundle = TestTools.getMessages("it");
        lt = new JLanguageTool(new Italian());

        // Remove the previous tokens file before outputting a new conl file.
        String tokenFileName = "tokens.conl";
        File file = new File(tokenFileName);
        file.delete();
    }

    @Before
    public void ruleSetup() {
        this.rule = new AgreementRule(ItalianResourceBundle);
    }

    private void assertGood(String s) throws IOException {
        RuleMatch[] matches = rule.match(lt.getAnalyzedSentence(s));
        if (enableAssertions) assertThat(matches.length, is(0));
    }

    private void assertBad(String s) throws IOException {
        RuleMatch[] matches = rule.match(lt.getAnalyzedSentence(s));
        if (enableAssertions) assertThat(matches.length, is(1));
    }

    @Ignore
    @Test
    public void testAll() throws IOException {
        // Disable assertions before outputting conl file.
        enableAssertions = false;

        // Subject Verb Agreement Unit Tests
        test1_VPPS_AGREE_incorrect();
        test2_VPPS_AGREE_correct();
        test3_VPPS_AGREE_incorrect();
        test4_VPPS_AGREE_correct();
        test5_VPPS_AGREE_incorrect();
        test6_VPPS_AGREE_correct();
        test7_VPPS_AGREE_incorrect();
        test8_VPPS_AGREE_correct();
        test9_VPPS_AGREE_incorrect();
        test10_VPPS_AGREE_correct();
        test11_VPPS_AGREE_incorrect();
        test12_VPPS_AGREE_correct();
        test13_VPPS_AGREE_incorrect();
        test14_VPPS_AGREE_correct();
        test15_VPPS_AGREE_incorrect();
        test16_VPPS_AGREE_correct();
        test17_VPPS_AGREE_incorrect();
        test18_VPPS_AGREE_correct();
        test19_VPPS_AGREE_incorrect();
        test20_VPPS_AGREE_correct();
        test21_VPPS_AGREE_incorrect();
        test22_VPPS_AGREE_correct();
        test23_VPPS_AGREE_incorrect();
        test24_VPPS_AGREE_correct();
        test25_VPPS_AGREE_incorrect();
        test26_VPPS_AGREE_correct();
        test27_VPPS_AGREE_incorrect();
        test28_VPPS_AGREE_correct();
        test29_VPPS_AGREE_incorrect();
        test30_VPPS_AGREE_correct();
        test31_VPPDOP_AGREE_incorrect();
        test32_VPPDOP_AGREE_correct();
        test33_VPPS_AGREE_incorrect();
        test34_VPPS_AGREE_correct();
        test35_VPPDOP_AGREE_incorrect();
        test36_VPPDOP_AGREE_correct();
        test37_VPPDOP_AGREE_incorrect();
        test38_VPPDOP_AGREE_correct();
        test39_VPPDOP_AGREE_incorrect();
        test40_VPPDOP_AGREE_correct();
        test41_VPPDOP_AGREE_incorrect();
        test42_VPPDOP_AGREE_correct();

        // Article Agreement Unit Tests
        test1_ART_AGREE_incorrect();
        test2_ART_AGREE_correct();
        test3_ART_AGREE_incorrect();
        test4_ART_AGREE_correct();
        test5_ART_AGREE_incorrect();
        test6_ART_AGREE_correct();
        test7_ART_AGREE_incorrect();
        test8_ART_AGREE_correct();
        test9_ART_FORM_incorrect();
        test10_ART_FORM_correct();
        test11_ART_FORM_incorrect();
        test12_ART_FORM_correct();
        test13_ART_FORM_incorrect();
        test14_ART_FORM_correct();
        test15_ART_FORM_incorrect();
        test16_ART_FORM_correct();
        test17_ART_FORM_incorrect();
        test18_ART_FORM_correct();
        test19_ART_FORM_incorrect();
        test20_ART_FORM_correct();
        test21_ART_REQUIRED_incorrect();
        test22_ART_REQUIRED_correct();
        test23_ART_REQUIRED_incorrect();
        test24_ART_REQUIRED_correct();
        test25_ART_REQUIRED_incorrect();
        test26_ART_REQUIRED_correct();
        test27_ART_REQUIRED_incorrect();
        test28_ART_REQUIRED_correct();
        test29_ART_REQUIRED_incorrect();
        test30_ART_REQUIRED_correct();
        test31_ART_REQUIRED_incorrect();
        test32_ART_REQUIRED_correct();
        test33_ART_REQUIRED_incorrect();
        test34_ART_REQUIRED_correct();
        test35_ART_REQUIRED_incorrect();
        test36_ART_REQUIRED_correct();
        test37_ART_REQUIRED_incorrect();
        test38_ART_REQUIRED_correct();
        test39_ART_REQUIRED_incorrect();
        test40_ART_REQUIRED_correct();
        test41_ART_TITLE_incorrect();
        test42_ART_TITLE_correct();
        test43_ART_TITLE_incorrect();
        test44_ART_TITLE_correct();
        test45_ART_TITLE_incorrect();
        test46_ART_TITLE_correct();
        test47_ART_TUTTO_incorrect();
        test48_ART_TUTTO_correct();
        test49_ART_TUTTO_incorrect();
        test50_ART_TUTTO_correct();
        test51_ART_TUTTO_incorrect();
        test52_ART_TUTTO_correct();
        test53_ART_TUTTO_incorrect();
        test54_ART_TUTTO_correct();
        test55_ART_BODY_incorrect();
        test56_ART_BODY_correct();
        test57_ART_BODY_incorrect();
        test58_ART_BODY_correct();
        test59_ART_BODY_incorrect();
        test60_ART_BODY_correct();
        test61_ART_BODY_incorrect();
        test62_ART_BODY_correct();
        test63_ART_BODY_incorrect();
        test64_ART_BODY_correct();
        test65_ART_BODY_incorrect();
        test66_ART_BODY_correct();
        test67_ART_PROPER_incorrect();
        test68_ART_PROPER_correct();
        test69_ART_PROPER_incorrect();
        test70_ART_PROPER_correct();
        test71_ART_PROPER_incorrect();
        test72_ART_PROPER_correct();
        test73_ART_PROPER_incorrect();
        test74_ART_PROPER_correct();
        test75_ART_PROPER_incorrect();
        test76_ART_PROPER_correct();
        test77_ART_PROPER_incorrect();
        test78_ART_PROPER_correct();
        test79_ART_PROPER_incorrect();
        test80_ART_PROPER_correct();
        test81_ART_PROPER_incorrect();
        test82_ART_PROPER_correct();
        test85_ART_APPOSITIVE_incorrect();
        test86_ART_APPOSITIVE_correct();
        test87_ART_APPOSITIVE_incorrect();
        test88_ART_APPOSITIVE_correct();
        test89_ART_APPOSITIVE_incorrect();
        test90_ART_APPOSITIVE_correct();
        test91_ART_TIME_incorrect();
        test92_ART_TIME_correct();
        test93_ART_TIME_incorrect();
        test94_ART_TIME_correct();
        test95_ART_TIME_incorrect();
        test96_ART_TIME_correct();
        test97_ART_TIME_incorrect();
        test98_ART_TIME_correct();
        test99_ART_TIME_incorrect();
        test100_ART_TIME_correct();
        test101_ART_TIME_incorrect();
        test102_ART_TIME_correct();
        test103_ART_KIN_incorrect();
        test104_ART_KIN_correct();
        test105_ART_KIN_incorrect();
        test106_ART_KIN_correct();
        test107_ART_KIN_incorrect();
        test108_ART_KIN_correct();
        test109_ART_KIN_incorrect();
        test110_ART_KIN_correct();
        test111_ART_KIN_incorrect();
        test112_ART_KIN_correct();
        test113_ART_KIN_incorrect();
        test114_ART_KIN_correct();
        test115_ART_KIN_incorrect();
        test116_ART_KIN_correct();
        test117_ART_KIN_incorrect();
        test118_ART_KIN_correct();
        test119_ART_KIN_incorrect();
        test120_ART_KIN_correct();
        test121_ART_KIN_incorrect();
        test122_ART_KIN_correct();
        test123_ART_KIN_incorrect();
        test124_ART_KIN_correct();
        test135_ART_PAIR_LIST_incorrect();
        test136_ART_PAIR_LIST_correct();
        test137_ART_PAIR_LIST_incorrect();
        test138_ART_PAIR_LIST_correct();
        test139_ART_PAIR_LIST_incorrect();
        test140_ART_PAIR_LIST_correct();
        test143_ART_W_PREP_incorrect();
        test144_ART_W_PREP_correct();
        test145_ART_W_PREP_incorrect();
        test146_ART_W_PREP_correct();
        test147_ART_SUPERL_ADJ_incorrect();
        test148_ART_SUPERL_ADJ_correct();
        test149_ART_SUPERL_ADJ_incorrect();
        test150_ART_SUPERL_ADJ_correct();
        test151_ART_SUPERL_ADV_incorrect();
        test152_ART_SUPERL_ADV_correct();
        test153_ART_SUPERL_ADV_incorrect();
        test154_ART_SUPERL_ADV_correct();
        test155_ART_OMITTED_incorrect();
        test156_ART_OMITTED_correct();
        test157_ART_OMITTED_incorrect();
        test158_ART_OMITTED_correct();
    }

    // Verb Agreement Unit Tests

    @Test // Dependency Graph
    public void test1_VPPS_AGREE_incorrect() throws IOException {
        // I have explained the lesson and she has followed very carefully.
        assertBad("Ho spiegato la lezione e lei ha seg\u00ADuita molto attentamente.");
        // Detected two errors instead of one.

        // Error One: False Positive
        // Ho is an auxiliary verb with a lemma of avere.
        // speigato is a past participle verb and should simply be masculine, singular.
        // The dependency graph incorrectly thinks la is a direct obj when in fact it is a noun.
        // La is feminine while spiegato is masculine, and fail the AvereAgreement rule.

        // Error Two: True Positive
        // Ha is an auxiliary verb with a lemma of avere.
        // seguita is a past participle verb without a direct object.
        // It should be (and is) masculine and singular.

    }

    @Test // Disambiguation
    public void test2_VPPS_AGREE_correct() throws IOException {
        // I have explained the lesson and she has followed very carefully.
        assertGood("Ho spiegato la lezione e lei ha seguito molto attentamente.");

        // Error: False Positive
        // Ho is an auxiliary verb with a lemma of avere.
        // speigato is a past participle verb and should simply be masculine, singular.
        // The dependency graph correctly marks "la" as the obj of the sentence.
        // Because "La" can be a personal pronoun, it's covered by the Avere Agreement Rule.
        // La is feminine while spiegato is masculine, and fails the AvereAgreement rule.
        // It's actually an article in a noun phrase, but needs disambiguation.
    }

    @Test
    public void test3_VPPS_AGREE_incorrect() throws IOException {
        // My sons have emigrated.
        assertBad("I miei figli sono emigrato all'estero.");

        // Emigrato is singular.
        // I miei figli is plural.
        // Because sono has the lemma of essere, verb and subject need to agree.
        // If the auxiliary verb had a lemma of avere, they would not need to agree. (?)
    }

    @Test // Disambiguation
    public void test4_VPPS_AGREE_correct() throws IOException {
        // My sons have emigrated.
        assertGood("I miei figli sono emigrati all'estero.");

        // Fails Noun-Adjective Agreement test.
        // emigrati can be a masculine plural noun, while estero can be a masculine, singular, positive adjective.
    }

    @Test // Disambiguation
    public void test5_VPPS_AGREE_incorrect() throws IOException {
        // That car cost me an arm and a leg (lit. an eye from my head).
        assertBad("Quella macchina mi è costato un occhio della testa.");

        // Costato: *1: VERB - Indicative, first person, singular, present
        //           2: VERB - Masculine, singular, past participle
        // SUBJECT
        // Quella:  *1: DET-DEMO - Feminine, singular
        //          *2: PRO-DEMO - Feminine, singular
    }

    @Test
    public void test6_VPPS_AGREE_correct() throws IOException {
        // That car cost me an arm and a leg (lit. an eye from my head).
        assertGood("Quella macchina mi è costata un occhio della testa.");
    }

    @Test
    public void test7_VPPS_AGREE_incorrect() throws IOException {
        // St. Peter's square had seemed to her an enchanted kingdom.
        assertBad("Piazza San Pietro a lei era sembrato un regno incantato.");
    }

    @Test
    public void test8_VPPS_AGREE_correct() throws IOException {
        // St. Peter's square had seemed to her an enchanted kingdom.
        assertGood("Piazza San Pietro a lui era sembrata un regno incantato.");
    }

    @Test  // Dependency Graph
    public void test9_VPPS_AGREE_incorrect() throws IOException {
        // The twin girls were born on May 6.
        assertBad("Le gemelle erano nato il 6 maggio.");

        // The verb of the sentence is not being linked directly to the subject, so it does not
        // qualify for subject-verb agreement validation.

        // TODO: First word ("Le") is being assigned dependency label of ROOT, which is not in the treebank???
    }

    @Test // Dependency Graph
    public void test10_VPPS_AGREE_correct() throws IOException {
        // The twin girls were born on May 6.
        assertGood("Le gemelle erano nate il 6 maggio.");
        // TODO: This has not yet been tested.  Test and analyze results.
    }

    @Test
    public void test11_VPPS_AGREE_incorrect() throws IOException {
        // Dragons have never existed outside fairy tales.
        assertBad("Non sono mai esistito draghi fuori dalle favole.");
    }

    @Test
    public void test12_VPPS_AGREE_correct() throws IOException {
        // Dragons have never existed outside fairy tales.
        assertGood("Non sono mai esistiti draghi fuori dalle favole.");
    }

    @Test
    public void test13_VPPS_AGREE_incorrect() throws IOException {
        // We liked your poems (lit. Your poems were pleasing to us.)
        assertBad("Ci sono piaciuto le tue poesie.");
    }

    @Test
    public void test14_VPPS_AGREE_correct() throws IOException {
        // We liked your poems (lit. Your poems were pleasing to us.)
        assertGood("Ci sono piaciute le tue poesie.");
    }

    @Test
    public void test15_VPPS_AGREE_incorrect() throws IOException {
        // The bricklayers started at 9 and finished at 3.
        assertBad("I muratori hanno cominciati alle otto e finito alle tre.");
        // hanno is avere.
        // cominciati must take masculine singular form when no direct object pronoun is present.
        // muratori is masculine plural
        // cominciati is masculine plural
    }

    @Test
    public void test16_VPPS_AGREE_correct() throws IOException {
        // The bricklayers started at 9 and finished at 3.
        assertGood("I muratori hanno cominciato alle otto e finito alle tre.");
        // With hanno (a form of avere), the subject verb don't need to match.
        // muratori is masculine plural
        // cominciato is masculine singular.
    }

    @Test // Dependency Graph
    public void test17_VPPS_AGREE_incorrect() throws IOException {
        // You had to go away.
        assertBad("Ve ne siete dovuto andare.");

        // Siete is an auxiliary verb with a lemma of essere.
        // Dovuto is the main verb and should agree with the subject.
        // Ve is the subject, but is not linked to the verb in the dependency graph.
    }

    @Test
    public void test18_VPPS_AGREE_correct() throws IOException {
        // You had to go away.
        assertGood("Ve ne siete dovuti andare.");
    }

    @Test // Dependency Graph
    public void test19_VPPS_AGREE_incorrect() throws IOException {
        // You had to go away.
        assertBad("Avete dovuti andarvene.");

        // Avete is the auxiliary verb and has a lemma of avere.
        // Main verb is dovuti (masculine plural) and must be masculine
        // singular since it doesn't have a direct object pronoun.
        // The dependency graph doesn't link the auxiliary verb to the main verb.
    }

    @Test
    public void test20_VPPS_AGREE_correct() throws IOException {
        // You had to go away.
        assertGood("Avete dovuto andarvene.");
    }

    @Test
    public void test21_VPPS_AGREE_incorrect() throws IOException {
        // All these things were able to be sold at a good price.
        assertBad("Tutte queste cose hanno potute essere vendute a buon prezzo.");
    }

    @Test
    public void test22_VPPS_AGREE_correct() throws IOException {
        // All these things were able to be sold at a good price.
        assertGood("Tutte queste cose hanno potuto essere vendute a buon prezzo.");
    }

    @Test // Dependency Graph
    public void test23_VPPS_AGREE_incorrect() throws IOException {
        // When did you get up, girls?
        assertBad("A che ora vi siete alzato, ragazze?");

        // Siete is a form of essere.  Subject and verb need to match.
        // Verb is alzato. Graph is missing a link between subject and verb.
        // Vi is the subject.
    }

    @Test
    public void test24_VPPS_AGREE_correct() throws IOException {
        // When did you get up, girls?
        assertGood("A che ora vi siete alzate, ragazze?");
    }

    @Test
    public void test25_VPPS_AGREE_incorrect() throws IOException {
        // Maria bought the skirt.
        assertBad("Maria ha comprata la gonna.");
    }

    @Test
    public void test26_VPPS_AGREE_correct() throws IOException {
        // Maria bought the skirt.
        assertGood("Maria ha comprato la gonna.");
    }

    @Test
    public void test27_VPPS_AGREE_incorrect() throws IOException {
        // The apples were bought by Luisa.
        assertBad("Le mele sono comprato da Luisa.");
    }

    @Test
    public void test28_VPPS_AGREE_correct() throws IOException {
        // The apples were bought by Luisa.
        assertGood("Le mele sono comprate da Luisa.");
    }

    @Test // Dependency Graph
    public void test29_VPPS_AGREE_incorrect() throws IOException {
        // As soon as were seated in the room, the film began. (lit. the film was started)
        assertBad("Appena ci siamo seduto in sala, è iniziato il film.");

        // No errors detected.  Should be one.
        // Siamo is an auxiliary verb with the lemma of essere, meaning verb and subject must agree.
        // Seduto is the main verb and should agree with the personal pronoun "ci".
        // Currently "ci" and "seduto" don't agree, but the disagreement violation is not detected
        // because the graph does not link the verb to the subject.
    }

    @Test
    public void test30_VPPS_AGREE_correct() throws IOException {
        // As soon as were seated in the room, the film began. (lit. the film was started)
        assertGood("Appena ci siamo seduti in sala, è iniziato il film.");
    }

    @Test // Dependency graph
    public void test31_VPPDOP_AGREE_incorrect() throws IOException {
        // Afterward she had given them to him.
        assertBad("E dopo, gliele avrebbe dato.");

        // False negative.  (No errors detected, one should be detected.)
        // Avrebbe is an auxiliary verb with a lemma of avere.
        // Dato is the past participle main verb (masculine, singular).
        // Gliele should be the direct object, and the main verb should be required to agree with it.
        // The dependency graph incorrectly marks the relationship between gliele and dato as an ARG.
        //  The Avere Agreement Rule fails to apply.
    }

    @Test // Disambiguation
    public void test32_VPPDOP_AGREE_correct() throws IOException {
        // Afterward she had given them to him.
        assertGood("E dopo, gliele avrebbe date.");

        // Error: False Positive.
        // "Date" is the main verb, but has a possible interpretation of a noun.
        // "Dopo" is linked to "date" as a RMOD (restricting modifier) and has a possible interpretation
        // as an adjective, which means that it is validated by the noun-adjective agreement rule.
    }

    @Test
    public void test33_VPPS_AGREE_incorrect() throws IOException {
        // Paul has cut his hair.
        assertBad("Paolo si è tagliato i capelli.");
    }

    @Test
    public void test34_VPPS_AGREE_correct() throws IOException {
        // Paul has cut his hair.
        assertGood("Paolo si è tagliati i capelli.");
    }

    @Test
    public void test35_VPPDOP_AGREE_incorrect() throws IOException {
        // Paul knew that two new discs had come out and he bought them.
        assertBad("Paolo ha saputo che sono usciti due nuovi dischi e li ha comprato.");
    }

    @Test
    public void test36_VPPDOP_AGREE_correct() throws IOException {
        // Paul knew that two new discs had come out and he bought them.
        assertGood("Paolo ha saputo che sono usciti due nuovi dischi e li ha comprati.");
    }

    @Test // Dependency Graph
    public void test37_VPPDOP_AGREE_incorrect() throws IOException {
        // Yes, I have cooked them.
        assertBad("Si, le ho cucinato.");

        // Error: False Negative
        // Ho is an auxiliary verb with a lemma of avere.
        // Cucinato is the past participle main verb (masculine/singular).
        // Le is the direct object pronoun (feminine/plural, feminine/singular).
        // Cucinato should be required to agree with le.
        // The dependency graph labels the relationship as "OBJ+SUBJ", which means
        // the Avere Agreement Rule fails to detect the violation.
    }

    @Test // Dependency Graph
    public void test38_VPPDOP_AGREE_correct() throws IOException {
        // Yes, I have cooked them.
        assertGood("Si, le ho cucinate.");

        // Error: False Positive
        // Ho is an auxiliary verb with the lemma of avere.
        // Cucinate is the main verb (feminine/plural).
        // Le is the direct object pronoun (feminine/plural) of cucinate.
        // Le and cucinate should and do agree.
        // The dependency graph labels the relationship between the main verb
        // and it's direct object pronoun as ARG, meaning it was not detected.
        // Main verbs without direct object pronouns are required to be masculine/singular,
        // so a violation was incorrectly detected.
    }

    @Test // Dependency Graph
    public void test39_VPPDOP_AGREE_incorrect() throws IOException {
        // No, we did not listen to it.
        assertBad("No, non le abbiamo ascoltato.");

        // Error: False Negative
        // Abbiamo is an auxiliary verb with the lemma of avere.
        // Ascoltato is the past participle main verb (masculine/singular).
        // Le is the direct object pronoun of the main verb (feminine/plural, feminine/singular).
        // Le and ascoltato should (but don't) agree.
        // The dependency graph incorrectly labels the relationship between abbiamo and ascoltato
        // as RMOD, which means the Avere Agreement Rule is not applied.
        // Instead the verb is checked against a subject, but none are linked in the dependency graph,
        // so agreement is not checked at all for ascoltato.
    }

    @Test
    public void test40_VPPDOP_AGREE_correct() throws IOException {
        // No, we did not listen to it.
        assertGood("No, non le abbiamo ascoltate.");
    }

    @Test // Dependency Graph
    public void test41_VPPDOP_AGREE_incorrect() throws IOException {
        // We met them.
        assertBad("Le abbiamo incontrato.");

        // Error: False Negative
        // Abbiamo is an auxiliary verb with the lemma of Avere.
        // Incontrato is a past participle main verb (masculine/singular).
        // Le is the main verb's direct object pronoun (feminine/plural, feminine/singular).
        // Le and incontrato should be required to (but don't) agree.
        // The dependency graph incorrectly labels the relationship between the verb
        // and it's auxiliary as RMOD, meaning the Avere Agreement Rule is not applied.
    }

    @Test
    public void test42_VPPDOP_AGREE_correct() throws IOException {
        // We met them.
        assertGood("Le abbiamo incontrate.");
    }

    // Article Agreement Unit Tests
    @Test
    public void test1_ART_AGREE_incorrect() throws IOException {
        // We never eat meat.
        assertBad("Non mangiamo mai il carne.");
    }

    @Test
    public void test2_ART_AGREE_correct() throws IOException {
        // We never eat meat.
        assertGood("Non mangiamo mai la carne.");
    }

    @Test
    public void test3_ART_AGREE_incorrect() throws IOException {
        // Money is the root of all evil.
        assertBad("Denaro è il fonte di tutti i mali.");
    }

    @Test
    public void test4_ART_AGREE_correct() throws IOException {
        // Money is the root of all evil.
        assertGood("Il denaro è la fonte di tutti i mali.");
    }

    @Test
    public void test5_ART_AGREE_incorrect() throws IOException {
        // Should I take the medicine before meals or after meals?
        assertBad("Devo prendere la medicina prima di pasti o dopo pasti?");
    }

    @Test
    public void test6_ART_AGREE_correct() throws IOException {
        // Should I take the medicine before meals or after meals?
        assertGood("Devo prendere la medicina prima dei pasti o dopo i pasti?");
    }

    @Test
    public void test7_ART_AGREE_incorrect() throws IOException {
        // Do you know the story of Pinocchio?
        assertBad("Conosci il storia di Pinocchio?");
    }

    @Test
    public void test8_ART_AGREE_correct() throws IOException {
        // Do you know the story of Pinocchio?
        assertGood("Conosci la storia di Pinocchio?");
    }

    @Test
    public void test9_ART_FORM_incorrect() throws IOException {
        // I don't like sports at all.
        assertBad("Il sport non mi piace per niente.");
    }

    @Test
    public void test10_ART_FORM_correct() throws IOException {
        // I don't like sports at all.
        assertGood("Lo sport non mi piace per niente.");
    }

    @Test
    public void test11_ART_FORM_incorrect() throws IOException {
        // Italians are nice.
        assertBad("I Italiani sono simpatici.");
    }

    @Test
    public void test12_ART_FORM_correct() throws IOException {
        // Italians are nice.
        assertGood("Gli Italiani sono simpatici.");
    }

    @Test
    public void test13_ART_FORM_incorrect() throws IOException {
        // The Greek islands are famous.
        assertBad("Gli isole greche sono famose.");
    }

    @Test
    public void test14_ART_FORM_correct() throws IOException {
        // The Greek islands are famous.
        assertGood("Le isole greche sono famose.");
    }

    @Test
    public void test15_ART_FORM_incorrect() throws IOException {
        // What are the colors of the Italian flag?
        assertBad("Quali sono le colori della bandiera italiana?");
    }

    @Test
    public void test16_ART_FORM_correct() throws IOException {
        // What are the colors of the Italian flag?
        assertGood("Quali sono i colori della bandiera italiana?");
    }

    @Test
    public void test17_ART_FORM_incorrect() throws IOException {
        // The nice [upscale] stores have lovely windows.
        assertBad("I bei negozi hanno i belle vetrine.");
    }

    @Test
    public void test18_ART_FORM_correct() throws IOException {
        // The nice [upscale] stores have lovely windows.
        assertGood("I bei negozi hanno le belle vetrine.");
    }

    @Test
    public void test19_ART_FORM_incorrect() throws IOException {
        // Tomorrow is my only opportunity to leave work early.
        assertBad("Domani è la unica occasione che posso lasciare lavoro in anticipo.");
    }

    @Test
    public void test20_ART_FORM_correct() throws IOException {
        // Tomorrow is my only opportunity to leave work early.
        assertGood("Domani è l'unica occasione che posso lasciare il lavoro in anticipo.");
    }

    @Test
    public void test21_ART_REQUIRED_incorrect() throws IOException {
        // I have to get a driver's license.
        assertBad("Devo prendere una patente.");
    }

    @Test
    public void test22_ART_REQUIRED_correct() throws IOException {
        // I have to get a driver's license.
        assertGood("Devo prendere la patente.");
    }

    @Test
    public void test23_ART_REQUIRED_incorrect() throws IOException {
        // There are automobiles even on the sidewalks!
        assertBad("Ci sono macchine anche su marciapiedi!");
    }

    @Test
    public void test24_ART_REQUIRED_correct() throws IOException {
        // There are automobiles even on the sidewalks!
        assertGood("Ci sono macchine anche sui marciapiedi!");
    }

    @Test
    public void test25_ART_REQUIRED_incorrect() throws IOException {
        // He stayed in bed because he had a cough.
        assertBad("Era rimasto a letto perchè aveva una tosse.");
    }

    @Test
    public void test26_ART_REQUIRED_correct() throws IOException {
        // He stayed in bed because he had a cough.
        assertGood("Era rimasto a letto perchè aveva la tosse.");
    }

    @Test
    public void test27_ART_REQUIRED_incorrect() throws IOException {
        // Men kid themselves that they can change the course of history.
        assertBad("Uomini si illudono di dare un corso diverso a storia.");
    }

    @Test
    public void test28_ART_REQUIRED_correct() throws IOException {
        // Men kid themselves that they can change the course of history.
        assertGood("Gli uomini si illudono di dare un corso diverso alla storia.");
    }

    @Test
    public void test29_ART_REQUIRED_incorrect() throws IOException {
        // Wine is bad for your health.
        assertBad("Vino fa male a salute.");
    }

    @Test
    public void test30_ART_REQUIRED_correct() throws IOException {
        // Wine is bad for your health.
        assertGood("Il vino fa male alla salute.");
    }

    @Test
    public void test31_ART_REQUIRED_incorrect() throws IOException {
        // Hatred is perhaps the most powerful of human passions.
        assertBad("Odio è forse la più potente di passioni umane.");
    }

    @Test
    public void test32_ART_REQUIRED_correct() throws IOException {
        // Hatred is perhaps the most powerful of human passions.
        assertGood("L'odio è forse la più potente delle passioni umane.");
    }

    @Test
    public void test33_ART_REQUIRED_incorrect() throws IOException {
        // My father is a carpenter.
        assertBad("Mio padre fa un falegname.");
    }

    @Test
    public void test34_ART_REQUIRED_correct() throws IOException {
        // My father is a carpenter.
        assertGood("Mio padre fa il falegname.");
    }

    @Test
    public void test35_ART_REQUIRED_incorrect() throws IOException {
        // Don't be an idiot.
        assertBad("Non fare uno scemo.");
    }

    @Test
    public void test36_ART_REQUIRED_correct() throws IOException {
        // Don't be an idiot.
        assertGood("Non fare lo scemo.");
    }

    @Test
    public void test37_ART_REQUIRED_incorrect() throws IOException {
        // His back hurts.
        assertBad("Gli fa male la sua schiena.");
    }

    @Test
    public void test38_ART_REQUIRED_correct() throws IOException {
        // His back hurts.
        assertGood("Gli fa male la schiena.");
    }

    @Test
    public void test39_ART_REQUIRED_incorrect() throws IOException {
        // Eigty percent of the students in my Italian class are girls.
        assertBad("Ottanta per cento degli studenti nella mia classe d'Italiano sono ragazze.");
    }

    @Test
    public void test40_ART_REQUIRED_correct() throws IOException {
        // Eigty percent of the students in my Italian class are girls.
        assertGood("L'ottanta per cento degli studenti nella mia classe d'Italiano sono ragazze.");
    }

    @Test
    public void test41_ART_TITLE_incorrect() throws IOException {
        // I'm sorry but Dr. Zambetti hasn't arrived yet.
        assertBad("Mi dispiace ma dottor Zambetti non è ancora arrivato.");
    }

    @Test
    public void test42_ART_TITLE_correct() throws IOException {
        // I'm sorry but Dr. Zambetti hasn't arrived yet.
        assertGood("Mi dispiace ma il dottor Zambetti non è ancora arrivato.");
    }

    @Test
    public void test43_ART_TITLE_incorrect() throws IOException {
        // Good morning, Dr. Zambetti.
        assertBad("Buon giorno, il dottor Zambetti.");
    }

    @Test
    public void test44_ART_TITLE_correct() throws IOException {
        // Good morning, Dr. Zambetti.
        assertGood("Buon giorno, dottor Zambetti.");
    }

    @Test
    public void test45_ART_TITLE_incorrect() throws IOException {
        // My teacher goes to Italy every year.
        assertBad("Mio professore va in Italia tutti gli anni.");
    }

    @Test
    public void test46_ART_TITLE_correct() throws IOException {
        // My teacher goes to Italy every year.
        assertGood("Il mio professore va in Italia tutti gli anni.");
    }

    @Test
    public void test47_ART_TUTTO_incorrect() throws IOException {
        // We have already seen both movies.
        assertBad("Abbiamo già visto tutti e due film.");
    }

    @Test
    public void test48_ART_TUTTO_correct() throws IOException {
        // We have already seen both movies.
        assertGood("Abbiamo già visto tutti e due i film.");
    }

    @Test
    public void test49_ART_TUTTO_incorrect() throws IOException {
        // All his relatives live in the south of Italy.
        assertBad("Tutti suoi parenti abitano nel sud d'Italia.");
    }

    @Test
    public void test50_ART_TUTTO_correct() throws IOException {
        // All his relatives live in the south of Italy.
        assertGood("Tutti i suoi parenti abitano nel sud d'Italia.");
    }

    @Test
    public void test51_ART_TUTTO_incorrect() throws IOException {
        // He likes to spend all summer with his grandmother.
        assertBad("Gli piace trascorrere tutta estate con la nonna.");
    }

    @Test
    public void test52_ART_TUTTO_correct() throws IOException {
        // He likes to spend all summer with his grandmother.
        assertGood("Gli piace trascorrere tutta l'estate con la nonna.");
    }

    @Test
    public void test53_ART_TUTTO_incorrect() throws IOException {
        // My teacher goes to Italy every year.
        assertBad("Il mio professore va in Italia tutti anni.");
    }

    @Test
    public void test54_ART_TUTTO_correct() throws IOException {
        // My teacher goes to Italy every year.
        assertGood("Il mio professore va in Italia tutti gli anni.");
    }

    @Test
    public void test55_ART_BODY_incorrect() throws IOException {
        // One must recognize his own mistakes.
        assertBad("Bisogna riconoscere suoi propri errori.");
    }

    @Test
    public void test56_ART_BODY_correct() throws IOException {
        // One must recognize his own mistakes.
        assertGood("Bisogna riconoscere i propri errori.");
    }

    @Test
    public void test57_ART_BODY_incorrect() throws IOException {
        // Every pupil must raise his hand to ask to go out.
        assertBad("Ciascuno alunno deve alzare sua mano per chiedere di uscire.");
    }

    @Test
    public void test58_ART_BODY_correct() throws IOException {
        // Every pupil must raise his hand to ask to go out.
        assertGood("Ciascuno alunno deve alzare la mano per chiedere di uscire.");
    }

    @Test
    public void test59_ART_BODY_incorrect() throws IOException {
        // The cat has scratched your face.
        assertBad("Il gatto ha graffiato la tua faccia.");
    }

    @Test
    public void test60_ART_BODY_correct() throws IOException {
        // The cat has scratched your face.
        assertGood("Il gatto ti ha graffiato la faccia.");
    }

    @Test
    public void test61_ART_BODY_incorrect() throws IOException {
        // Maria paints her nails.
        assertBad("Maria si dipinge sue unghie.");
    }

    @Test
    public void test62_ART_BODY_correct() throws IOException {
        // Maria paints her nails.
        assertGood("Maria si dipinge le unghie.");
    }

    @Test
    public void test63_ART_BODY_incorrect() throws IOException {
        // My teeth hurt.
        assertBad("I miei denti mi dolgono.");
    }

    @Test
    public void test64_ART_BODY_correct() throws IOException {
        // My teeth hurt.
        assertGood("Mi dolgono i denti.");
    }

    @Test
    public void test65_ART_BODY_incorrect() throws IOException {
        // Paul raised his head from (his) books.
        assertBad("Paolo alzò la sua testa dai libri.");
    }

    @Test
    public void test66_ART_BODY_correct() throws IOException {
        // Paul raised his head from (his) books.
        assertGood("Paolo alzò la testa dai libri.");
    }

    @Test
    public void test67_ART_PROPER_incorrect() throws IOException {
        // Have you met Alfredo?
        assertBad("Hai incontrato l'Alfredo?");
    }

    @Test
    public void test68_ART_PROPER_correct() throws IOException {
        // Have you met Alfredo?
        assertGood("Hai incontrato Alfredo?");
    }

    @Test
    public void test69_ART_PROPER_incorrect() throws IOException {
        // My favorite city is Venice.
        assertBad("La mia città preferita è la Venezia.");
    }

    @Test
    public void test70_ART_PROPER_correct() throws IOException {
        // My favorite city is Venice.
        assertGood("La mia città preferita è Venezia.");
    }

    @Test
    public void test71_ART_PROPER_incorrect() throws IOException {
        // The Rome of my youth was very different.
        assertBad("Roma della mia gioventù era molto diversa.");
    }

    @Test
    public void test72_ART_PROPER_correct() throws IOException {
        // The Rome of my youth was very different.
        assertGood("La Roma della mia gioventù era molto diversa.");
    }

    @Test
    public void test73_ART_PROPER_incorrect() throws IOException {
        // Do you remember poor Antonio?
        assertBad("Ti ricordi povero Antonio?");
    }

    @Test
    public void test74_ART_PROPER_correct() throws IOException {
        // Do you remember poor Antonio?
        assertGood("Ti ricordi il povero Antonio?");
    }

    @Test
    public void test75_ART_PROPER_incorrect() throws IOException {
        // Trastevere is a very typical district of Rome.
        assertBad("Il Trastevere è un quartiere molto caratteristico della Roma.");
    }

    @Test
    public void test76_ART_PROPER_correct() throws IOException {
        // Trastevere is a very typical district of Rome.
        assertGood("Trastevere è un quartiere molto caratteristico di Roma.");
    }

    @Test
    public void test77_ART_PROPER_incorrect() throws IOException {
        // Via Condotti is the luxury shoppping street in Rome.
        assertBad("La via Condotti è la via dello shopping di lusso alla Roma.");
    }

    @Test
    public void test78_ART_PROPER_correct() throws IOException {
        // Via Condotti is the luxury shoppping street in Rome.
        assertGood("Via Condotti è la via dello shopping di lusso a Roma.");
    }

    @Test
    public void test79_ART_PROPER_incorrect() throws IOException {
        // Tokyo is a very large city.
        assertBad("Il Tokyo è una città molto grande.");
    }

    @Test
    public void test80_ART_PROPER_correct() throws IOException {
        // Tokyo is a very large city.
        assertGood("Tokyo è una città molto grande.");
    }

    @Test
    public void test81_ART_PROPER_incorrect() throws IOException {
        // Tuscany is the region I know best.
        assertBad("Per me, Toscana è la ragione che conosco meglio.");
    }

    @Test
    public void test82_ART_PROPER_correct() throws IOException {
        // Tuscany is the region I know best.
        assertGood("Per me, la Toscana è la ragione che conosco meglio.");
    }

/*    @Test
    public void test83_ART_APPOSITIVE_incorrect() throws IOException {
        //
        assertBad("");
    }*/

    /*@Test
    public void test84_ART_APPOSITIVE_correct() throws IOException {
        //
        assertGood("camera da letto");
        // TODO: Incomplete sentence.  Needs to be removed.
    }*/

    @Test
    public void test85_ART_APPOSITIVE_incorrect() throws IOException {
        // Your friend has been elected chairman of the meeting.
        assertBad("Il tuo amico è stato eletto il presidente dell'assemblea.");
    }

    @Test
    public void test86_ART_APPOSITIVE_correct() throws IOException {
        // Your friend has been elected chairman of the meeting.
        assertGood("Il tuo amico è stato eletto presidente dell'assemblea.");
    }

    @Test
    public void test87_ART_APPOSITIVE_incorrect() throws IOException {
        // He called me a thief.
        assertBad("Mi ha chiamato un ladro.");
    }

    @Test
    public void test88_ART_APPOSITIVE_correct() throws IOException {
        // He called me a thief.
        assertGood("Mi ha chiamato ladro.");
    }

    @Test
    public void test89_ART_APPOSITIVE_incorrect() throws IOException {
        // Rome, capital of Italy, is rich in history.
        assertBad("Roma, la capitale d'Italia, è ricca di storia.");
    }

    @Test
    public void test90_ART_APPOSITIVE_correct() throws IOException {
        // Rome, capital of Italy, is rich in history.
        assertGood("Roma, capitale d'Italia, è ricca di storia.");
    }

    @Test
    public void test91_ART_TIME_incorrect() throws IOException {
        // From four to seven I work in the garden.
        assertBad("Da quattro a sette lavoro in giardino.");
    }

    @Test
    public void test92_ART_TIME_correct() throws IOException {
        // From four to seven I work in the garden.
        assertGood("Dalle quattro alle sette lavoro in giardino.");
    }

    @Test
    public void test93_ART_TIME_incorrect() throws IOException {
        // On Tuesdays and Fridays the signora goes to town.
        assertBad("Martedì e venerdì la signora va in città.");
    }

    @Test
    public void test94_ART_TIME_correct() throws IOException {
        // On Tuesdays and Fridays the signora goes to town.
        assertGood("Il martedì e il venerdì la signora va in città.");
    }

    @Test
    public void test95_ART_TIME_incorrect() throws IOException {
        // Next Tuesday, she will vist her daughter instead.
        assertBad("Il martedì prossimo, invece, visiterà sua figlia.");
    }

    @Test
    public void test96_ART_TIME_correct() throws IOException {
        // Next Tuesday, she will vist her daughter instead.
        assertGood("Martedì prossimo, invece, visiterà la figlia.");
    }

    @Test
    public void test97_ART_TIME_incorrect() throws IOException {
        // It is eleven o'clock.
        assertBad("Sono undici.");
    }

    @Test
    public void test98_ART_TIME_correct() throws IOException {
        // It is eleven o'clock.
        assertGood("Sono le undici.");
    }

    @Test
    public void test99_ART_TIME_incorrect() throws IOException {
        // The express for Torino departs at ten thirty from track twelve.
        assertBad("Il rapido per Torino parte a dieci e trenta dal binario dodici.");
    }

    @Test
    public void test100_ART_TIME_correct() throws IOException {
        // The express for Torino departs at ten thirty from track twelve.
        assertGood("Il rapido per Torino parte alle dieci e trenta dal binario dodici.");
    }

    @Test
    public void test101_ART_TIME_incorrect() throws IOException {
        // January is usually a very cold month in Italy.
        assertBad("Il gennaio è un mese di solito molto freddo in Italia.");
    }

    @Test
    public void test102_ART_TIME_correct() throws IOException {
        // January is usually a very cold month in Italy.
        assertGood("Gennaio è un mese di solito molto freddo in Italia.");
    }

    @Test
    public void test103_ART_KIN_incorrect() throws IOException {
        // Do your aunts live here?
        assertBad("Abitano qui tue zie?");
    }

    @Test
    public void test104_ART_KIN_correct() throws IOException {
        // Do your aunts live here?
        assertGood("Abitano qui le tue zie?");
    }

    @Test
    public void test105_ART_KIN_incorrect() throws IOException {
        // Claudio looks for his white shirt in his sister's room.
        assertBad("Claudio cerca la sua camicia bianca nella stanza della sua sorella.");
    }

    @Test
    public void test106_ART_KIN_correct() throws IOException {
        // Claudio looks for his white shirt in his sister's room.
        assertGood("Claudio cerca la sua camicia bianca nella stanza di sua sorella.");
    }

    @Test
    public void test107_ART_KIN_incorrect() throws IOException {
        // How young your mom is!
        assertBad("Com'è giovane tua mamma.");
    }

    @Test
    public void test108_ART_KIN_correct() throws IOException {
        // How young your mom is!
        assertGood("Com'è giovane la tua mamma.");
    }

    @Test
    public void test109_ART_KIN_incorrect() throws IOException {
        // Their father works abroad.
        assertBad("Loro padre lavora all'estero.");
    }

    @Test
    public void test110_ART_KIN_correct() throws IOException {
        // Their father works abroad.
        assertGood("Il loro padre lavora all'estero.");
    }

    @Test
    public void test111_ART_KIN_incorrect() throws IOException {
        // Your son really loves his sister.
        assertBad("Vostro figlio ama davvero la sua sorella.");
    }

    @Test
    public void test112_ART_KIN_correct() throws IOException {
        // Your son really loves his sister.
        assertGood("Il vostro figlio ama davvero sua sorella.");
    }

    @Test
    public void test113_ART_KIN_incorrect() throws IOException {
        // We sons love our dear mother.
        assertBad("Noi figli amiamo nostra cara mamma.");
    }

    @Test
    public void test114_ART_KIN_correct() throws IOException {
        // We sons love our dear mother.
        assertGood("Noi figli amiamo la nostra cara mamma.");
    }

    @Test
    public void test115_ART_KIN_incorrect() throws IOException {
        // I have never met (I never knew) my cousin from Naples.
        assertBad("Non ho mai conosciuto mio cugino di Napoli.");
    }

    @Test
    public void test116_ART_KIN_correct() throws IOException {
        // I have never met (I never knew) my cousin from Naples.
        assertGood("Non ho mai conosciuto il mio cugino di Napoli.");
    }

    @Test
    public void test117_ART_KIN_incorrect() throws IOException {
        // What mother doesn't love her children?
        assertBad("Quale madre non ama suoi figli?");
    }

    @Test
    public void test118_ART_KIN_correct() throws IOException {
        // What mother doesn't love her children?
        assertGood("Quale madre non ama i suoi figli?");
    }

    @Test
    public void test119_ART_KIN_incorrect() throws IOException {
        // This is my book. (This book is mine).
        assertBad("Questo libro è il mio.");
    }

    @Test
    public void test120_ART_KIN_correct() throws IOException {
        // This is my book. (This book is mine).
        assertGood("Questo libro è mio.");
    }

    @Test
    public void test121_ART_KIN_incorrect() throws IOException {
        // His wife is German, did you know?
        assertBad("La sua moglie è tedesca, lo sapevi?");
    }

    @Test
    public void test122_ART_KIN_correct() throws IOException {
        // His wife is German, did you know?
        assertGood("Sua moglie è tedesca, lo sapevi?");
    }

    @Test
    public void test123_ART_KIN_incorrect() throws IOException {
        // What is your (little) brother's name?
        assertBad("Allora, come si chiama tuo fratellino?");
    }

    @Test
    public void test124_ART_KIN_correct() throws IOException {
        // What is your (little) brother's name?
        assertGood("Allora, come si chiama il tuo fratellino?");
    }

/*    @Test
    public void test125_ART_CASA_incorrect() throws IOException {
        //
        assertBad("");
    }*/

/*    @Test
    public void test126_ART_CASA_correct() throws IOException {
        //
        assertGood("");
    }*/

/*    @Test
    public void test127_ART_CASA_incorrect() throws IOException {
        //
        assertBad("");
    }*/

/*    @Test
    public void test128_ART_CASA_correct() throws IOException {
        //
        assertGood("");
    }*/

/*    @Test
    public void test129_ART_CASA_incorrect() throws IOException {
        //
        assertBad("");
    }*/

/*    @Test
    public void test130_ART_CASA_correct() throws IOException {
        //
        assertGood("");
    }*/

/*    @Test
    public void test131_ART_CASA_incorrect() throws IOException {
        //
        assertBad("");
    }*/

/*    @Test
    public void test132_ART_CASA_correct() throws IOException {
        //
        assertGood("");
    }*/

/*    @Test
    public void test133_ART_CASA_incorrect() throws IOException {
        //
        assertBad("");
    }*/

/*    @Test
    public void test134_ART_CASA_correct() throws IOException {
        //
        assertGood("");
    }*/

    @Test
    public void test135_ART_PAIR_LIST_incorrect() throws IOException {
        // The airplane flew over cities, lakes, mountains, seas, and islands.
        assertBad(":L'aeroplano sorvolò le città, i laghi, i monti, i mari e le isole.");
    }

    @Test
    public void test136_ART_PAIR_LIST_correct() throws IOException {
        // The airplane flew over cities, lakes, mountains, seas, and islands.
        assertGood(":L'aeroplano sorvolò città, laghi, monti, mari e isole.");
    }

    @Test
    public void test137_ART_PAIR_LIST_incorrect() throws IOException {
        // Could I have a little (some) bread and cheese?
        assertBad("Potrei avere un po' della pane e del formaggio?");
    }

    @Test
    public void test138_ART_PAIR_LIST_correct() throws IOException {
        // Could I have a little (some) bread and cheese?
        assertGood("Potrei avere un po' di pane e formaggio?");
    }

    @Test
    public void test139_ART_PAIR_LIST_incorrect() throws IOException {
        // I have brought the passport, the tickets, the itinerary, and the cellphone.
        assertBad("Ho portato il passaporto, i biglietti, l'agenda, il cellulare.");
    }

    @Test
    public void test140_ART_PAIR_LIST_correct() throws IOException {
        // I have brought the passport, the tickets, the itinerary, and the cellphone.
        assertGood("Ho portato passaporto, biglietti, agenda, cellulare.");
    }

    /*@Test
    public void test141_ART_W_PREP_incorrect() throws IOException {
        //
        assertBad("");
    }*/

    /*@Test
    public void test142_ART_W_PREP_correct() throws IOException {
        //
        assertGood("in realtà, in apparenza, una statua di marmo");
        // TODO: Incomplete sentence, can be removed.
    }*/

    @Test
    public void test143_ART_W_PREP_incorrect() throws IOException {
        // They reacted calmly.
        assertBad("Hanno reagito con la calma.");
    }

    @Test
    public void test144_ART_W_PREP_correct() throws IOException {
        // They reacted calmly.
        assertGood("Hanno reagito con calma.");
    }

    @Test
    public void test145_ART_W_PREP_incorrect() throws IOException {
        // Tomorrow morning I'm not going to school, I'm staying home.
        assertBad("Domani mattina non vado alla scuola, rimango a casa.");
    }

    @Test
    public void test146_ART_W_PREP_correct() throws IOException {
        // Tomorrow morning I'm not going to school, I'm staying home.
        assertGood("Domani mattina non vado a scuola, rimango a casa.");
    }

    @Test
    public void test147_ART_SUPERL_ADJ_incorrect() throws IOException {
        // It's the longest movie I have [ever] seen.
        assertBad("È il film il più lungo che io abbia visto.");
    }

    @Test
    public void test148_ART_SUPERL_ADJ_correct() throws IOException {
        // It's the longest movie I have [ever] seen.
        assertGood("È il film più lungo che io abbia visto.");
    }

    @Test
    public void test149_ART_SUPERL_ADJ_incorrect() throws IOException {
        // The most intelligent boy in the family never went to college.
        assertBad("Il ragazzo il più intelligente della famiglia non è mai andato all'università.");
    }

    @Test
    public void test150_ART_SUPERL_ADJ_correct() throws IOException {
        // The most intelligent boy in the family never went to college.
        assertGood("Il ragazzo più intelligente della famiglia non è mai andato all'università.");
    }

    @Test
    public void test151_ART_SUPERL_ADV_incorrect() throws IOException {
        // He spoke the most rapidly of all (of anyone).
        assertBad("Ha parlato più il rapidamente di tutti.");
    }

    @Test
    public void test152_ART_SUPERL_ADV_correct() throws IOException {
        // He spoke the most rapidly of all (of anyone).
        assertGood("Ha parlato più rapidamente di tutti.");
    }

    @Test
    public void test153_ART_SUPERL_ADV_incorrect() throws IOException {
        // I returned home as soon as possible.
        assertBad("Sono toranto a casa più presto possibile.");
    }

    @Test
    public void test154_ART_SUPERL_ADV_correct() throws IOException {
        // I returned home as soon as possible.
        assertGood("Sono tornato a casa il più presto possibile.");
    }

    @Test
    public void test155_ART_OMITTED_incorrect() throws IOException {
        // I don't read any newspapers.
        assertBad("Non leggo i giornali.");
    }

    @Test
    public void test156_ART_OMITTED_correct() throws IOException {
        // I don't read any newspapers.
        assertGood("Non leggo giornali.");
    }

    @Test
    public void test157_ART_OMITTED_incorrect() throws IOException {
        // Our dog is always hungry.
        assertBad("Il nostro cane sempre ha la fame.");
    }

    @Test
    public void test158_ART_OMITTED_correct() throws IOException {
        // Our dog is always hungry.
        assertGood("Il nostro cane sempre ha fame.");
    }
}
