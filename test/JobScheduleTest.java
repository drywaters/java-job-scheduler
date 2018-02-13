import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class JobScheduleTest {

	private JobSchedule schedule;
	
	@Before
	public void setup() {
		schedule = new JobSchedule();
	}
	
	@Test
	public void givenTest() {
		schedule.addJob(8);
		schedule.addJob(3);
		schedule.addJob(5);
		
		schedule.getJob(0).requires(schedule.getJob(2));
		schedule.getJob(0).requires(schedule.getJob(1));
		
		assertEquals(13, schedule.minCompletionTime());
		assertEquals(5, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());
	
	
		schedule.getJob(1).requires(schedule.getJob(2));
		
		assertEquals(16, schedule.minCompletionTime());
		
		assertEquals(8, schedule.getJob(0).getStartTime());
		assertEquals(5, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());	
		
		schedule.getJob(1).requires(schedule.getJob(0));
	
		
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.minCompletionTime());
		
		assertEquals(-1, schedule.getJob(0).getStartTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());
	}
	
	@Test
	public void singleJobRequiresFive() {
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		
		schedule.getJob(0).requires(schedule.getJob(1));
		schedule.getJob(0).requires(schedule.getJob(2));
		schedule.getJob(0).requires(schedule.getJob(3));
		schedule.getJob(0).requires(schedule.getJob(4));
		schedule.getJob(0).requires(schedule.getJob(5));

		assertEquals(0, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(4).getStartTime());
		assertEquals(0, schedule.getJob(3).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(1).getStartTime());
		assertEquals(50, schedule.getJob(0).getStartTime());
		
		assertEquals(50, schedule.minCompletionTime());
	}
	
	@Test
	public void treeOfJobs() {
//								 40
//							 /        \
//							30        50
//	                      /   \      /   \
//						10    20    60   70	
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(60);
		schedule.addJob(70);
		
		schedule.getJob(4).requires(schedule.getJob(3));
		schedule.getJob(4).requires(schedule.getJob(5));
		schedule.getJob(3).requires(schedule.getJob(1));
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(5).requires(schedule.getJob(6));
		schedule.getJob(5).requires(schedule.getJob(7));
				
		assertEquals(160, schedule.minCompletionTime());
		assertEquals(120, schedule.getJob(4).getStartTime());
		assertEquals(20, schedule.getJob(3).getStartTime());
		assertEquals(70, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(6).getStartTime());
		assertEquals(0, schedule.getJob(7).getStartTime());
	}

	@Test
	public void treeOfJobsWithRightCycle() {
//								 40
//							 /        \
//							30         50
//	                      /   \      \/  /\
//						10    20    60 -> 70	
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(60);
		schedule.addJob(70);
		
		schedule.getJob(3).requires(schedule.getJob(1));
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(4).requires(schedule.getJob(3));
		schedule.getJob(4).requires(schedule.getJob(5));
		schedule.getJob(5).requires(schedule.getJob(7));
		schedule.getJob(6).requires(schedule.getJob(5));
		schedule.getJob(7).requires(schedule.getJob(6));
		
		assertEquals(-1, schedule.minCompletionTime());

		assertEquals(-1, schedule.getJob(4).getStartTime());
		assertEquals(20, schedule.getJob(3).getStartTime());
		assertEquals(-1, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(2).getStartTime());
		assertEquals(-1, schedule.getJob(6).getStartTime());
		assertEquals(-1, schedule.getJob(7).getStartTime());
	}
	
	@Test
	public void treeOfJobsWithTwoCycles() {
//								 40
//							 /\       /\
//							 30       50
//	                       \/  /\    \/  /\
//						  10 -> 20   60 -> 70	
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(60);
		schedule.addJob(70);
		
		schedule.getJob(3).requires(schedule.getJob(1));
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(4).requires(schedule.getJob(3));
		schedule.getJob(4).requires(schedule.getJob(5));
		schedule.getJob(5).requires(schedule.getJob(7));
		schedule.getJob(6).requires(schedule.getJob(5));
		schedule.getJob(7).requires(schedule.getJob(6));
		
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(1).requires(schedule.getJob(3));
		
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(4).getStartTime());
		assertEquals(-1, schedule.getJob(3).getStartTime());
		assertEquals(-1, schedule.getJob(5).getStartTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(-1, schedule.getJob(2).getStartTime());
		assertEquals(-1, schedule.getJob(6).getStartTime());
		assertEquals(-1, schedule.getJob(7).getStartTime());
	}

	@Test
	public void treeOfJobsWithLeftCycle() {
//								 40
//							 /        \
//							30         50
//	                      \/  /\      /  \
//						10 -> 20    60   70	
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(60);
		schedule.addJob(70);
		
		schedule.getJob(4).requires(schedule.getJob(3));
		schedule.getJob(4).requires(schedule.getJob(5));
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(1).requires(schedule.getJob(3));
		schedule.getJob(5).requires(schedule.getJob(7));
		schedule.getJob(5).requires(schedule.getJob(6));
		
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(4).getStartTime());
		assertEquals(-1, schedule.getJob(3).getStartTime());
		assertEquals(-1, schedule.getJob(2).getStartTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(70, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(6).getStartTime());
		assertEquals(0, schedule.getJob(7).getStartTime());
	}
	
	@Test
	public void randomTreeWithNoCycleWithOneHugeJob() {
//						  30
//					      /\       
//						  20         
//	                      /\          
//						  10    40   1000(Job 5)
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(1000);
		
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(2).requires(schedule.getJob(1));
		
		assertEquals(1000, schedule.minCompletionTime());
		assertEquals(30, schedule.getJob(3).getStartTime());
		assertEquals(10, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(1).getStartTime());
		assertEquals(0, schedule.getJob(4).getStartTime());
		assertEquals(0, schedule.getJob(5).getStartTime());
	}
	
	@Test
	public void randomTreeWithOneCycleWithOneHugeJob() {
//						  30
//					    \/ /\       
//						40  20         
//	                    \/  /\          
//						  10    50   1000(Job 6)
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(1000);
		
		schedule.getJob(1).requires(schedule.getJob(4));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(3).requires(schedule.getJob(2));
		schedule.getJob(4).requires(schedule.getJob(3));
		
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(-1, schedule.getJob(2).getStartTime());
		assertEquals(-1, schedule.getJob(3).getStartTime());
		assertEquals(-1, schedule.getJob(4).getStartTime());
		assertEquals(0, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(6).getStartTime());
	}

	@Test
	public void oneGiantCycle() {
//								40
//							 \/   /\
//							30     50
//	                      \/         /\
//						  10          70
//		                   \/        /\
//		                    20  ->  60    
		
		schedule.addJob(0);
		schedule.addJob(10);
		schedule.addJob(20);
		schedule.addJob(30);
		schedule.addJob(40);
		schedule.addJob(50);
		schedule.addJob(60);
		schedule.addJob(70);
		
		schedule.getJob(1).requires(schedule.getJob(3));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(3).requires(schedule.getJob(4));
		schedule.getJob(4).requires(schedule.getJob(5));
		schedule.getJob(5).requires(schedule.getJob(7));
		schedule.getJob(6).requires(schedule.getJob(2));
		schedule.getJob(7).requires(schedule.getJob(6));

		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(-1, schedule.getJob(2).getStartTime());
		assertEquals(-1, schedule.getJob(3).getStartTime());
		assertEquals(-1, schedule.getJob(4).getStartTime());
		assertEquals(-1, schedule.getJob(5).getStartTime());
		assertEquals(-1, schedule.getJob(6).getStartTime());
		assertEquals(-1, schedule.getJob(7).getStartTime());
	}
	
	@Test
	public void oneLargeManual() {	
		for (int i = 0; i <= 13; i++) {
			schedule.addJob(i);
		}
		
		schedule.getJob(2).requires(schedule.getJob(0));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(2).requires(schedule.getJob(7));
		schedule.getJob(0).requires(schedule.getJob(7));
		schedule.getJob(7).requires(schedule.getJob(10));
		
		assertEquals(19, schedule.minCompletionTime());
		assertEquals(17, schedule.getJob(2).getStartTime());
		assertEquals(19, schedule.minCompletionTime());
		assertEquals(17, schedule.getJob(0).getStartTime());
		assertEquals(19, schedule.minCompletionTime());
		
		schedule.getJob(7).requires(schedule.getJob(6));
	
		assertEquals(19, schedule.minCompletionTime());
		assertEquals(17, schedule.getJob(2).getStartTime());
		assertEquals(19, schedule.minCompletionTime());
		assertEquals(17, schedule.getJob(0).getStartTime());
		assertEquals(19, schedule.minCompletionTime());
		
		schedule.getJob(6).requires(schedule.getJob(11));
		
		assertEquals(26, schedule.minCompletionTime());
		assertEquals(24, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(10).getStartTime());
		assertEquals(17, schedule.getJob(7).getStartTime());
		assertEquals(11, schedule.getJob(6).getStartTime());
		assertEquals(0, schedule.getJob(11).getStartTime());
		assertEquals(24, schedule.getJob(2).getStartTime());
		
		schedule.getJob(11).requires(schedule.getJob(8));
		
		assertEquals(34, schedule.minCompletionTime());
		assertEquals(32, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(10).getStartTime());
		assertEquals(25, schedule.getJob(7).getStartTime());
		assertEquals(19, schedule.getJob(6).getStartTime());
		assertEquals(8, schedule.getJob(11).getStartTime());
		assertEquals(32, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(8).getStartTime());
		
		schedule.getJob(5).requires(schedule.getJob(2));
		
		assertEquals(39, schedule.minCompletionTime());
		assertEquals(32, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(10).getStartTime());
		assertEquals(25, schedule.getJob(7).getStartTime());
		assertEquals(19, schedule.getJob(6).getStartTime());
		assertEquals(8, schedule.getJob(11).getStartTime());
		assertEquals(32, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(8).getStartTime());
		assertEquals(34, schedule.getJob(5).getStartTime());
				
		schedule.getJob(11).requires(schedule.getJob(4));
		schedule.getJob(1).requires(schedule.getJob(10));
		
		assertEquals(39, schedule.minCompletionTime());
		assertEquals(32, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(10).getStartTime());
		assertEquals(25, schedule.getJob(7).getStartTime());
		assertEquals(19, schedule.getJob(6).getStartTime());
		assertEquals(8, schedule.getJob(11).getStartTime());
		assertEquals(32, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(8).getStartTime());
		assertEquals(34, schedule.getJob(5).getStartTime());
		
		schedule.getJob(11).requires(schedule.getJob(12));
		
		assertEquals(43, schedule.minCompletionTime());
		assertEquals(36, schedule.getJob(0).getStartTime());
		assertEquals(0, schedule.getJob(10).getStartTime());
		assertEquals(10, schedule.getJob(1).getStartTime());
		assertEquals(29, schedule.getJob(7).getStartTime());
		assertEquals(23, schedule.getJob(6).getStartTime());
		assertEquals(12, schedule.getJob(11).getStartTime());
		assertEquals(36, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(8).getStartTime());
		assertEquals(38, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(12).getStartTime());
		
		// cycle
		schedule.getJob(10).requires(schedule.getJob(1));
		
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(0).getStartTime());
		assertEquals(-1, schedule.getJob(10).getStartTime());
		assertEquals(-1, schedule.getJob(1).getStartTime());
		assertEquals(-1, schedule.getJob(7).getStartTime());
		assertEquals(23, schedule.getJob(6).getStartTime());
		assertEquals(12, schedule.getJob(11).getStartTime());
		assertEquals(-1, schedule.getJob(2).getStartTime());
		assertEquals(0, schedule.getJob(8).getStartTime());
		assertEquals(-1, schedule.getJob(5).getStartTime());
		assertEquals(0, schedule.getJob(12).getStartTime());
		
		assertEquals(-1, schedule.minCompletionTime());
		
		schedule.addJob(1000);
		assertEquals(-1, schedule.minCompletionTime());
		schedule.getJob(10).requires(schedule.getJob(13));
		assertEquals(-1, schedule.minCompletionTime());
		assertEquals(-1, schedule.getJob(10).getStartTime());
		schedule.getJob(4).requires(schedule.getJob(13));
		assertEquals(-1, schedule.getJob(10).getStartTime());
		assertEquals(-1, schedule.minCompletionTime());	
		assertEquals(13, schedule.getJob(4).getStartTime());
		schedule.getJob(12).requires(schedule.getJob(4));
		
		assertEquals(-1, schedule.minCompletionTime());	
		assertEquals(17, schedule.getJob(12).getStartTime());
		assertEquals(29, schedule.getJob(11).getStartTime());
		assertEquals(40, schedule.getJob(6).getStartTime());
		
		schedule.addJob(15);
	}
	
	@Test
	public void cycleNoChangeInStartTime() {
		schedule.addJob(5);
		schedule.addJob(0);
		schedule.addJob(7);
		schedule.addJob(9);
		
		schedule.getJob(1).requires(schedule.getJob(0));
		
		schedule.getJob(0).requires(schedule.getJob(2));
		schedule.getJob(2).requires(schedule.getJob(3));
		schedule.getJob(2).requires(schedule.getJob(1));
		assertEquals(-1, schedule.minCompletionTime());	
		
	}
	
	@Test
	public void cycleChangeInStartTime() {
		schedule.addJob(5);
		schedule.addJob(0);
		schedule.addJob(7);
		schedule.addJob(9);
		
		schedule.getJob(1).requires(schedule.getJob(0));
		schedule.getJob(0).requires(schedule.getJob(2));
		schedule.getJob(2).requires(schedule.getJob(3));
		schedule.getJob(2).requires(schedule.getJob(1));
		schedule.getJob(0).getStartTime();
		assertEquals(-1, schedule.minCompletionTime());	
	}
			
	@Test
	public void oneGiantTree() {
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE/100000; i+=1) {
			schedule.addJob(i);
		}
		
		schedule.getJob(2).requires(schedule.getJob(0));

		assertEquals(Integer.MAX_VALUE/100000-1, schedule.minCompletionTime());
		long endTime = System.currentTimeMillis();
		long difference = endTime - startTime;
		System.out.println("Time Difference = " + difference );
	}
			
}
