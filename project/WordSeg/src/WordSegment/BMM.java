
package WordSegment;

import java.util.Vector;


public class BMM extends SegStrategy {

		public Vector<String> Segment(String sentence, Dictionary dic) {
		int maxLength = dic.getMaxLength();	//字典中最长词的长度
		int negPos = sentence.length();
		int targetLength = maxLength;
		int restLength = sentence.length();
		Vector<String> seged = new Vector<String>();

		while (restLength > 0)
		{
			if (targetLength > restLength)
				targetLength = restLength;
			String tempStr = sentence.substring(negPos - targetLength, negPos);
			if (dic.checkWord(tempStr) || targetLength == 1)
			{
				seged.add(0, tempStr);
				negPos -= targetLength;
				restLength -= targetLength;
				targetLength = maxLength;				
			}
			else
				targetLength--;
		}
		return seged;
	}
}
