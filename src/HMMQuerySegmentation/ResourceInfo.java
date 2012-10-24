package HMMQuerySegmentation;
import java.util.HashSet;
import java.util.Set;




public class ResourceInfo {

	/**
	 * @author Saedeeh Shekarpour
	 */
	
		private String label;
		private String uri;
		
		private int rowNumber; 
		private int type = 0;
		private int connectivityDegree = -1;
		
		private double stringSimilarityScore;
		private double logConnectivityDegree=0;
		private double normalizedDegree;
		private Set<String> ClassSet=new HashSet();
		
		private double hub =1;
		private double authorithy =1;
		
		
		public double gethub()
		{
			return hub;
			
		}
		
		public double getauthorithy()
		{
			return authorithy;
			
		}
		
		
		public void sethub(double s)
		{
			 hub =s;
			
		}
		
		public void setauthorithy(double s)
		{
			 authorithy = s;
			
		}
		
		public String toString() {
			
			return "Label: " + this.label + " Uri: " + this.uri;
		}
		
		/**
		 * 
		 */
		public ResourceInfo(){
			
			this.type = Constants.TYPE_INSTANCE;
			this.connectivityDegree = -1;
			this.logConnectivityDegree = 0;
		}
		
		/**
		 * @return the label
		 */
		public String getLabel() {
			return label;
		}
		/**
		 * @param label the label to set
		 */
		public void setLabel(String label) {
			this.label = label;
		}
		/**
		 * @return the uri
		 */
		public String getUri() {
			return uri;
		}
		/**
		 * @return the ClassSet
		 */
		public Set<String> getClassSet() {
			return ClassSet;
		}
		/**
		 * @param uri the uri to set
		 */
		public void setClassSet(  HashSet<String> CS) {
			this.ClassSet.addAll(CS);
		}
		/**
		 * @param uri the uri to set
		 */
		public void setUri(String uri) {
			this.uri = uri;
		}
		/**
		 * @return the rowNumber
		 */
		public int getRowNumber() {
			return rowNumber;
		}
		/**
		 * @param rowNumber the rowNumber to set
		 */
		public void setRowNumber(int rowNumber) {
			this.rowNumber = rowNumber;
		}
		/**
		 * @return the type
		 */
		public int getType() {
			return type;
		}
		/**
		 * @param type the type to set
		 */
		public void setType(int type) {
			this.type = type;
		}
		/**
		 * @return the connectivityDegree
		 */
		public int getConnectivityDegree() {
			return connectivityDegree;
		}
		/**
		 * @param connectivityDegree the connectivityDegree to set
		 */
		public void setConnectivityDegree(int connectivityDegree) {
			this.connectivityDegree = connectivityDegree;
		}
		/**
		 * @return the stringSimilarityScore
		 */
		public double getStringSimilarityScore() {
			return stringSimilarityScore;
		}
		/**
		 * @param stringSimilarityScore the stringSimilarityScore to set
		 */
		public void setStringSimilarityScore(double stringSimilarityScore) {
			this.stringSimilarityScore = stringSimilarityScore;
		}
		/**
		 * @return the logConnectivityDegree
		 */
		public double getLogConnectivityDegree() {
			logConnectivityDegree=Math.log10((double)connectivityDegree);
			return logConnectivityDegree;
		}
		/**
		 * @param logConnectivityDegree the logConnectivityDegree to set
		 */
		public void setLogConnectivityDegree(double logConnectivityDegree) {
			this.logConnectivityDegree = logConnectivityDegree;
		}
		/**
		 * @return the normalizedDegree
		 */
		public double getNormalizedDegree() {
			return normalizedDegree;
		}
		/**
		 * @param normalizedDegree the normalizedDegree to set
		 */
		public void setNormalizedDegree(double normalizedDegree) {
			this.normalizedDegree = normalizedDegree;
		}
	}