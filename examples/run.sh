#!/usr/bin/env bash
if [ "$GITHUB_ACTIONS" = "true" ]; then
  version=$(cat ../version.sbt | sed -E 's/^.+"(.+)".*$/\1/')
else
  version=$(curl --silent https://api.github.com/repos/NomadBlacky/digdag-plugin-datadog/releases/latest | grep '"tag_name"' | sed -E 's/.+"v(.+)".+/\1/')
fi

echo "Plugin version: $version"

digdag run --no-save --param home=$HOME --param version=$version events.dig
