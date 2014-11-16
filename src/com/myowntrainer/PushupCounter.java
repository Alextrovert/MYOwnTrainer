package com.myowntrainer;

public class PushupCounter extends ExerciseCounter {
	
	@Override
	public void interpretData() {
		if (Po.size() > 120) {
			int fistcnt = 0, restcnt = 0;
			float ravg1 = 0, ravg2 = 0;
			float pavg1 = 0, pavg2 = 0;
			
			//get the average of the earliest few
			
			for (int i = 0; i < sampleSize; i++) {
				restcnt += (Po.get(i).equals("REST") ? 1 : 0);
				fistcnt += (Po.get(i).equals("REST") ? 0 : 1);
				ravg1 += R.get(i);
				pavg1 += P.get(i);
			}
			ravg1 /= sampleSize;
			pavg2 /= sampleSize;
			
			//get the average of the last few
			int hi = R.size() - 1;
			int lo = R.size() - sampleSize;
			for (int i = hi; i >= lo; i--) {
				fistcnt += (Po.get(i).equals("REST") ? 0 : 1);
				restcnt += (Po.get(i).equals("REST") ? 1 : 0);
				ravg2 += R.get(i);
				pavg2 += P.get(i);
			}
			ravg2 /= sampleSize;
			pavg2 /= sampleSize;
			
			//try and see if there's a major change in the roll
			if (Math.abs(ravg2 - ravg1) >= 40.0 || ravg2 < -100) {
				addRep();
			}
		}
	}
}
