#!/bin/bash
openssl aes-256-cbc -K $encrypted_526465573959_key -iv $encrypted_526465573959_iv -in prepare_environment.sh.enc -out prepare_environment.sh -d
bash prepare_environment.sh
./gradlew
./gradlew uploadArchives
