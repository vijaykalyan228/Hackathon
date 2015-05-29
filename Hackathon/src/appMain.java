import com.hackathon.helper.arffconverter;
import com.hackathon.helper.ParseTreeClassifier;


public class appMain {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		String input = "\\\\113.128.161.143\\Hackathon\\ProgramInput\\HackathonInput.txt";
		String output = "\\\\113.128.161.143\\Hackathon\\ProgramOutput\\Maverics1_Out.txt";
		arffconverter.convert(input);
		ParseTreeClassifier.getScore(input, output);
	}
}
