/**
 * Copyright (c) 2015 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
project {
    build {
        $execute(id: 'test1', phase: 'compile') {
            println 'hi'
        }
    }
}
