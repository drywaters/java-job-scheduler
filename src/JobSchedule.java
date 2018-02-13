import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class JobSchedule {

	private static int jobId;
	private List<Job> jobs;
	private int completionTime;
	private boolean hasCycle;
	public boolean hasChanged;
	
	public JobSchedule() {
		jobId = 0;
		jobs = new ArrayList<>();
		hasChanged = false;
		hasCycle = false;
		completionTime = 0;
	}
	
	public Job addJob(int time) {
		Job tempJob = new Job(jobId++, time);
		jobs.add(tempJob);
		return tempJob;
	}
	
	public Job getJob(int index) {
		return jobs.get(index);
	}
	
	public int minCompletionTime() {
		if (hasCycle) {
			return completionTime;
		}
		
		if (hasChanged) {
			jobs.get(0).calculateJobTimes(true);
		}
				
		return completionTime;
	}
		
	class Job {
		
		private int jobId;
		private int jobTime;
		private boolean isDiscoverable;
		int startTime;
	
		private List<Job> outgoingJobs;
		private List<Job> incomingJobs;
		
		private Job(int id, int jobTime) {
			outgoingJobs = new LinkedList<>();
			incomingJobs = new LinkedList<>();
			jobId = id;
			this.jobTime = jobTime;
			isDiscoverable = true;
			startTime = 0;
			if (jobTime > completionTime && !hasCycle) {
				completionTime = jobTime;
			}
		}
		
		public void requires(Job job) {
			incomingJobs.add(job);
			job.outgoingJobs.add(this);
			if (!hasChanged)
				hasChanged = hasChangeAffectedTimes(job, this);
		}
		
		private boolean hasChangeAffectedTimes(Job prevJob, Job nextJob) {
			return nextJob.startTime < prevJob.startTime + prevJob.jobTime;
		}

		public int getStartTime() {

			// Recalculate times if a new 
			// requirement was added that 
			// changes values
			
			if (!isDiscoverable) {
				this.startTime = -1;
				return startTime;
			}
			
			if (hasChanged) {
				calculateJobTimes(false);
			}
			
			if (!isDiscoverable) {
				this.startTime = -1;
			}

			return startTime;
		}
		
		private void calculateJobTimes(boolean isCompletionTimes) {
			resetJobs();
			LinkedList<Job> vertList = kahnTopOrder();
			
			// If only calculating completion times and find a cycle
			// then no need to recalculate the jobs, completion time 
			// will always be -1
			if (hasCycle && isCompletionTimes) {
				return;
			}
			
			relaxAllEdges(vertList);
			
			hasChanged = false;
		}

		private void relaxAllEdges(LinkedList<Job> vertList) {
			Iterator<Job> listIter = vertList.iterator();
			while (listIter.hasNext()) {
				Job nextJob = listIter.next();
				Iterator<Job> outIter = nextJob.outgoingJobs.iterator();
				while (outIter.hasNext()) {
					Job outgoingJob = outIter.next();
					relax(nextJob, outgoingJob);
				}
			}
		}
		
		private void relax(Job prevJob, Job nextJob) {
			if (nextJob.startTime < prevJob.startTime + prevJob.jobTime) {
				nextJob.startTime = prevJob.startTime + prevJob.jobTime;
				if (nextJob.startTime + nextJob.jobTime > completionTime && !hasCycle) {
					completionTime = nextJob.startTime + nextJob.jobTime;
				}					
			}		
		}
		
		private void resetJobs() {
			Iterator<Job> jobIter = jobs.iterator();
			while (jobIter.hasNext()) {
				Job nextJob = jobIter.next();
				nextJob.isDiscoverable = false;
				nextJob.startTime = 0;
			}
		}
		
		private LinkedList<Job> kahnTopOrder() {
			LinkedList<Job> topOrder = new LinkedList<Job>();
			int[] inDegree = new int[jobs.size()];
			Iterator<Job> jobIterator = jobs.iterator();
			int index = 0;
			
			// create list of number of incoming jobs
			while(jobIterator.hasNext()) {
				inDegree[index++] = jobIterator.next().incomingJobs.size();
			}
			
			// Enqueue all jobs with 0 incoming jobs.
			LinkedList<Job> jobQueue = new LinkedList<>();
			for (int i = 0; i < inDegree.length; i++) {
				if (inDegree[i] == 0) {
					jobQueue.add(jobs.get(i));
				}
			}

			while(jobQueue.size() != 0) {
				Job nextJob = jobQueue.poll();
				nextJob.isDiscoverable = true;
				topOrder.add(nextJob);
				Iterator<Job> outgoingJobsIter = nextJob.outgoingJobs.iterator();
				while(outgoingJobsIter.hasNext()) {
					Job outgoingJob = outgoingJobsIter.next();
					inDegree[outgoingJob.jobId]--;
					if (inDegree[outgoingJob.jobId] == 0) {
						jobQueue.add(outgoingJob);
					}
				}
			}
			
			// if sizes do not match, must have cycles
			if (topOrder.size() != jobs.size()) {
				hasCycle = true;
				completionTime = -1;
			}
		
			return topOrder;
		}
	}
}