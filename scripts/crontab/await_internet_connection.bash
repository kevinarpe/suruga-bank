#!/bin/bash --login

# Ex: /home/kca/saveme/git/suruga-bank
PROJECT_DIR_PATH="$(readlink -f "$(dirname "$(dirname "$(dirname "$0")")")")"
source "$PROJECT_DIR_PATH/scripts/bashlib" "$PROJECT_DIR_PATH/log" "$@"

main()
{
    while true
    do
        if bashlib_echo_and_run_cmd ping -c 1 www.google.com
        then
            break
        fi
        sleep 60
    done
}

main "$@"
