package com.myowntrainer;

public class JumpingjacksCounter extends ExerciseCounter {

	@Override
	public void update(String po, float r, float p, float y) {
		if (R.size() > 100) {
			Po.removeFirst();
			R.removeFirst();
			P.removeFirst();
			Y.removeFirst();
		}
		Po.add(po);
		R.add(r);
		P.add(p);
		Y.add(y);
		interpretData();
	}
	
	@Override
	public void interpretData() {
		if (Po.size() > 100) {
			
			int stage = 0, end = 0;
			/*
			 * stage 0: arms down
			 * stage 1: arms up
			 * stage 2: arms down
			 */
			if (!P.isEmpty() && P.get(0) > 50) for (int i = 5; i < 100; i++) {
				if (stage == 0 && P.get(i) < -50) {
					stage++;
				} else if (stage == 1 && P.get(i) > 50) {
					stage++;
					end = i;
				}
			}
			
			if (stage == 2) {
				totalReps++;
				for (int i = 0; i < end; i++) {
					Po.removeFirst();
					P.removeFirst();
					R.removeFirst();
					Y.removeFirst();
				}
			}
		}
	}
	
}
