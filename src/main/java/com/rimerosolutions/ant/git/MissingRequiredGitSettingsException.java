/*
 * Copyright 2015 Rimero Solutions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rimerosolutions.ant.git;

/**
 * Wrap possible NPE when git settings are expected.
 *
 * @author Yves Zoundi
 */
public class MissingRequiredGitSettingsException extends GitBuildException {

        private static final long serialVersionUID = -368892262593719576L;
        
        private static final String DEF_MSG = "Please configure the git settings 'settingsRef' attribute";
        
        public MissingRequiredGitSettingsException(String msg) {
                super(msg);
        } 
        
        public MissingRequiredGitSettingsException() {
                this(DEF_MSG);
        } 

}
