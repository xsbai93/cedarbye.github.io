package WordSegment;

import java.util.Vector;


public class FMM extends SegStrategy {

	//@Override
	public Vector<String> Segment(String sentence, Dictionary dic) {
		int maxLength = dic.getMaxLength();	//字典中最长词的长度
		int pos = 0;
		int targetLength = maxLength;
		int restLength = sentence.length();
		Vector<String> seged = new Vector<String>();

		while (restLength > 0)
		{
			if (targetLength > restLength)
				targetLength = restLength;
			String tempStr = sentence.substring(pos, pos + targetLength);
			if (dic.checkWord(tempStr) || targetLength == 1)
			{
				seged.add(tempStr);
				pos += targetLength;
				targetLength = maxLength;
				restLength = sentence.length() - pos;
			}
			else
				targetLength--;
		}
		return seged;
	}

}
