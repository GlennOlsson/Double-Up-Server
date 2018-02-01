#!/usr/bin/env bash
echo 'Trying to cd'
cd /home/tau/DoubleUpServer/Double-Up-Server || { echo 'cd failed' ; exit 1; }
echo 'Managed to cd, trying to git pull'
git pull || { echo 'git pull failed' ; exit 1; }
echo 'Managed to git pull, trying to exit running tmux'
tmux send-keys -t DoubleUp ^C || { echo 'Could not exit running script in tmux'; exit 1; }
echo 'Managed to exit running tmux script, trying to sleep for 5 seconds'
sleep 5
echo 'Done sleeping, trying to run and execute'
tmux send-keys -t DoubleUp 'gradle run' Enter || { echo 'Could not run'; exit 1; }
echo 'Managed send run and execute'
echo 'Successfully reset Double Up Server'