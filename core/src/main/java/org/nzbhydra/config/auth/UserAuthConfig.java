/*
 *  (C) Copyright 2017 TheOtherP (theotherp@gmx.de)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.nzbhydra.config.auth;

import lombok.Data;
import org.nzbhydra.config.BaseConfig;
import org.nzbhydra.config.ValidatingConfig;
import org.nzbhydra.config.sensitive.SensitiveData;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "auth.users")
public class UserAuthConfig extends ValidatingConfig<UserAuthConfig> {

    private static final String PASSWORD_ID = "{noop}";
    private boolean maySeeAdmin;
    private boolean maySeeDetailsDl;
    private boolean maySeeStats;
    private boolean showIndexerSelection;
    @SensitiveData
    private String username;
    @SensitiveData
    private String password;

    @Override
    public ConfigValidationResult validateConfig(BaseConfig oldConfig, UserAuthConfig newConfig) {
        return new ConfigValidationResult();
    }

    @Override
    public UserAuthConfig prepareForSaving() {
        if (password != null && !password.startsWith(PASSWORD_ID)) {
            password = PASSWORD_ID + password;
        }
        return this;
    }

    @Override
    public UserAuthConfig updateAfterLoading() {
        if (password != null && password.startsWith(PASSWORD_ID)) {
            password = password.substring(6);
        }
        return this;
    }

    @Override
    public UserAuthConfig initializeNewConfig() {
        return this;
    }
}
