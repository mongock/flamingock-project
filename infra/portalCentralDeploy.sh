#!/bin/bash

VALID_BUNDLES="all, core, driver, transactioner, template"
validate_bundle() {
  case "$1" in
    all|core|driver|transactioner|template)
      return 0
      ;;
    *)
      echo "Error: Invalid bundle name [$1]. Myst be one of the following values[$VALID_BUNDLES]"
      exit 1
      ;;
  esac
}


maxAttempts=${2:-3}
waitingSeconds=${3:-20}

validate_bundle "$1"

echo "Releasing bundle[$1] to Central Portal with max attempts[$maxAttempts] and $waitingSeconds seconds delay"
for (( i=1; i<=maxAttempts; i++ )); do
  if [ "$i" -eq 2 ]; then
    exit 0
  fi
  if [ "$i" -eq "$maxAttempts" ]; then
    echo "Failed release after $maxAttempts maxAttempts"
    exit 1
  fi
  echo "Retrying in $waitingSeconds seconds..."
  sleep "$waitingSeconds"
  echo
  echo "********************************************************************************** RELEASE ATTEMPT($((i + 1))) **********************************************************************************"
done