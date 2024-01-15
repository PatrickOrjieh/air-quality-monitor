#chat gpt was used as a reference for this code

#!/usr/bin/env python3
import subprocess

def update_system_packages():
    print("Updating system packages...")
    subprocess.run(['sudo', 'apt-get', 'update'], check=True)
    subprocess.run(['sudo', 'apt-get', 'upgrade', '-y'], check=True)

def update_pip_packages():
    print("Updating pip packages...")
    subprocess.run(['pip3', 'install', '--upgrade', 'pip'], check=True)
    subprocess.run(['pip3', 'list', '--outdated'], check=True)

if __name__ == "__main__":
    update_system_packages()
    update_pip_packages()
