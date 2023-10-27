/*
 * Copyright 2005-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.common.comm.osip.impl;

import org.openwms.common.comm.osip.OSIPComponent;
import org.openwms.common.comm.spi.FieldLengthProvider;

/**
 * A StaticFieldLengthProviderImpl.
 *
 * @author Heiko Scherrer
 */
@OSIPComponent
class StaticFieldLengthProviderImpl implements FieldLengthProvider {

    @Override
    public int lengthLocationGroupName() {
        return 20;
    }

    @Override
    public int barcodeLength() {
        return 20;
    }

    @Override
    public int locationIdLength() {
        return 20;
    }

    @Override
    public int noLocationIdFields() {
        return 5;
    }

}
