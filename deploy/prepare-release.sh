#!/bin/bash

set -e

export VERSION=$RELEASE_NUMBER.$TRAVIS_BUILD_NUMBER

if [[ $TRAVIS_BRANCH == "master" ]]; then
    git config user.email "mlehotsky13@gmail.com"
    git config user.name "mlehotsky13"
		git commit --allow-empty -m "Tag commit"
    git tag $VERSION -a -m "Tagged and released by Travis CI for build $TRAVIS_BUILD_NUMBER"
    git push origin $TRAVIS_BRANCH --tags
fi