package smartrics.ant.git;

import org.apache.tools.ant.BuildException;

/**
 *
 * @author yves
 */
public class GitBuildException extends BuildException {

        public GitBuildException(String msg, Exception e) {
                super(msg, e);
        }

        public GitBuildException(String msg) {
                super(msg);
        }
        
}
